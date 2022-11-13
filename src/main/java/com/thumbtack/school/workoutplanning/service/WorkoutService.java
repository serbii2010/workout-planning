package com.thumbtack.school.workoutplanning.service;

import com.thumbtack.school.workoutplanning.dto.request.workout.GenerateWorkoutDtoRequest;
import com.thumbtack.school.workoutplanning.dto.request.workout.WorkoutDtoRequest;
import com.thumbtack.school.workoutplanning.exception.BadRequestErrorCode;
import com.thumbtack.school.workoutplanning.exception.BadRequestException;
import com.thumbtack.school.workoutplanning.mappers.dto.WorkoutMapper;
import com.thumbtack.school.workoutplanning.model.AuthType;
import com.thumbtack.school.workoutplanning.model.RecordStatus;
import com.thumbtack.school.workoutplanning.model.User;
import com.thumbtack.school.workoutplanning.model.Workout;
import com.thumbtack.school.workoutplanning.repository.Operator;
import com.thumbtack.school.workoutplanning.repository.WorkoutRepository;
import com.thumbtack.school.workoutplanning.repository.WorkoutSpecificationsBuilder;
import com.thumbtack.school.workoutplanning.utils.AuthUtils;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@Transactional(rollbackOn = {Throwable.class})
@Slf4j
public class WorkoutService {
    @Autowired
    private UserService userService;
    @Autowired
    private WorkoutRepository workoutRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public Workout findById(Long id) throws BadRequestException {
        Optional<Workout> workout = workoutRepository.findById(id);
        if (workout.isEmpty()) {
            throw new BadRequestException(BadRequestErrorCode.WORKOUT_NOT_FOUND);
        }
        return workout.get();
    }

    public List<Workout> generateWorkout(GenerateWorkoutDtoRequest request) throws BadRequestException {
        String period = request.getPeriod();

        List<LocalDate> dates = new ArrayList<>();

        LocalDate dateStart = LocalDate.parse(request.getDateStart());
        LocalDate dateEnd = LocalDate.parse(request.getDateEnd());

        List<String> repeatedList = Arrays.asList(period.split(","));

        for (LocalDate date = dateStart; date.isBefore(dateEnd.plusDays(1)); date = date.plusDays(1)) {
            if (repeatedList.contains(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))) {
                dates.add(date);
                continue;
            }
            if (repeatedList.contains(String.valueOf(date.getDayOfMonth()))) {
                dates.add(date);
            }
        }

        User trainer = userService.findByUsername(request.getTrainer());
        if (trainer == null) {
            throw new BadRequestException(BadRequestErrorCode.TRAINER_NOT_FOUND);
        }

        List<Workout> workouts = new ArrayList<>();
        List<LocalDate> badDates = new ArrayList<>();
        dates.forEach(localDate -> {
            Workout workout = WorkoutMapper.INSTANCE.workoutDtoRequestToWorkout(request, localDate, trainer);
            if (isDuplicates(workout.getTimeStart(), localDate, workout.getDuration(), null)) {
                badDates.add(localDate);
            }
            workouts.add(workout);
        });

        if (!badDates.isEmpty()) {
            generateBadResponse(dates, request.getTimeStart(), request.getDuration());
        }
        return workoutRepository.saveAll(workouts);
    }

    public Workout update(Long id, WorkoutDtoRequest request) throws BadRequestException {
        if (workoutRepository.findById(id).isEmpty()) {
            throw new BadRequestException(BadRequestErrorCode.WORKOUT_NOT_FOUND);
        }
        Workout workout = findById(id);

        if (isDuplicates(LocalTime.parse(request.getTimeStart()), request.getDate(), request.getDuration(), workout)) {
            List<LocalDate> badDate = new ArrayList<>();
            badDate.add(request.getDate());
            generateBadResponse(badDate, request.getTimeStart(), request.getDuration());
        }

        WorkoutMapper.INSTANCE.update(workout, request, userService);
        workoutRepository.save(workout);
        return workout;
    }

    public List<Workout> filter(String trainerUsername, LocalDate dateStart, LocalDate dateEnd, LocalTime timeStart, LocalTime timeEnd, RecordStatus recordStatus) throws BadRequestException, AccessDeniedException {
        WorkoutSpecificationsBuilder builder = new WorkoutSpecificationsBuilder();

        if (trainerUsername != null) {
            User trainer = userService.findByUsername(trainerUsername);
            if (trainer == null) {
                throw new BadRequestException(BadRequestErrorCode.TRAINER_NOT_FOUND);
            }
            builder.with("trainer", Operator.EQUALS, trainer);
        }
        if (dateStart != null) {
            builder.with("date", Operator.LARGE, dateStart);
        }
        if (dateEnd != null) {
            builder.with("date", Operator.LESS, dateEnd);
        }
        if (timeStart != null) {
            builder.with("timeStart", Operator.LARGE, timeStart);
        }
        if (timeEnd != null) {
            builder.with("timeStart", Operator.LESS, timeEnd.plusSeconds(1));
        }
        if (recordStatus != null) {
            if (!AuthUtils.getRole().equals(AuthType.CLIENT)) {
                throw new AccessDeniedException("Action forbidden");
            }
            User user = userService.findByUsername(AuthUtils.getUsername());
            builder.with("user", Operator.EQUALS_USER, user);
            builder.with("status", Operator.EQUALS_STATUS, recordStatus);
        }
        Specification<Workout> specification = builder.build();
        return workoutRepository.findAll(specification);
    }

    public void delete(Long id) throws BadRequestException {
        workoutRepository.delete(findById(id));
    }

    private boolean isDuplicates(LocalTime timeStart, LocalDate localDate, Integer duration, Workout workout) {
        if (timeStart != null) {
            long count = workoutRepository.getCountWorkoutByTime(entityManager, localDate, timeStart, duration, workout);
            if (count > 0) {
                log.info(String.valueOf(count));
                return true;
            }
        }
        return false;
    }

    private void generateBadResponse(List<LocalDate> dates, String timeStart, Integer duration) throws BadRequestException {
        BadRequestErrorCode errorCode = BadRequestErrorCode.DATETIME_WORKOUT_ALREADY_EXISTS;

        String strError = new StringBuilder()
                .append("Workout by date [")
                .append(dates.stream().map(LocalDate::toString).collect(Collectors.joining(", ")))
                .append("] already exist in time [")
                .append(timeStart)
                .append("-")
                .append(LocalTime.parse(timeStart).plusMinutes(Long.valueOf(duration)))
                .append("].").toString();

        errorCode.setErrorString(strError);
        throw new BadRequestException(errorCode);
    }
}


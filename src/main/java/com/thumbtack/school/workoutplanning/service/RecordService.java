package com.thumbtack.school.workoutplanning.service;

import com.thumbtack.school.workoutplanning.exception.BadRequestErrorCode;
import com.thumbtack.school.workoutplanning.exception.BadRequestException;
import com.thumbtack.school.workoutplanning.model.AuthType;
import com.thumbtack.school.workoutplanning.model.Record;
import com.thumbtack.school.workoutplanning.model.RecordStatus;
import com.thumbtack.school.workoutplanning.model.User;
import com.thumbtack.school.workoutplanning.model.Workout;
import com.thumbtack.school.workoutplanning.repository.RecordRepository;
import com.thumbtack.school.workoutplanning.repository.UserRepository;
import com.thumbtack.school.workoutplanning.repository.WorkoutRepository;
import com.thumbtack.school.workoutplanning.utils.AuthUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@Transactional(rollbackOn = {RuntimeException.class}, dontRollbackOn = {AccessDeniedException.class})
@Slf4j
public class RecordService {
    @Autowired
    private RuleService ruleService;
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private WorkoutRepository workoutRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscriptionService subscriptionService;

    public List<Record> getRecordsByWorkout(Long id) {
        return workoutRepository.findById(id).orElseThrow(EntityNotFoundException::new).getRecords();
    }

    public List<Record> findAll() {
        return recordRepository.findAll();
    }

    public Record setStatus(Long workoutId, String username, RecordStatus status) throws BadRequestException {
        if (AuthUtils.getRole().equals(AuthType.CLIENT) && !AuthUtils.getUsername().equals(username)) {
            throw new BadRequestException(BadRequestErrorCode.INVALID_USERNAME, String.format("Username expected %s", AuthUtils.getUsername()));
        }
        Workout workout = workoutRepository.findById(workoutId).orElseThrow(() -> {
            throw new BadRequestException(BadRequestErrorCode.WORKOUT_NOT_FOUND);
        });

        if (AuthUtils.getRole() == AuthType.CLIENT) {
            if (LocalDateTime.of(workout.getDate(), workout.getTimeStart()).isBefore(LocalDateTime.now())) {
                throw new BadRequestException(BadRequestErrorCode.WORKOUT_ALREADY_STARTED);
            }
            if ( RecordStatus.CANCELLED.equals(status)) {
                ruleService.checkRules(workout);
            }
        }

        User client = userRepository.findByUsername(username);
        Record record = recordRepository.findByUserAndWorkout(client, workout);
        if (record == null) {
            return registerRecord(status, workout, client);
        }
        return updateStatus(status, workout, client, record);
    }

    public RecordStatus getStatusByClient(Long workoutId) {
        if (AuthUtils.getRole().equals(AuthType.CLIENT)) {
            User client = userRepository.findByUsername(AuthUtils.getUsername());
            Optional<Workout> workout = workoutRepository.findById(workoutId);
            if (client == null || workout.isEmpty()) {
                return RecordStatus.UNDEFINED;
            }
            Record record = recordRepository.findByUserAndWorkout(client, workout.get());
            if (record == null) {
                return RecordStatus.UNDEFINED;
            }
            return record.getStatus();
        }
        return RecordStatus.UNDEFINED;
    }

    private Record registerRecord(RecordStatus status, Workout workout, User client) {
        if (!RecordStatus.ACTIVE.equals(status) && !RecordStatus.QUEUED.equals(status)) {
            throw new BadRequestException(BadRequestErrorCode.BAD_STATUS);
        }
        subscriptionService.countdown(client, workout.getDate());
        if (RecordStatus.ACTIVE.equals(status)) {
            workout.setAvailableSeats(workout.getAvailableSeats() - 1);
        }

        workoutRepository.save(workout);
        Record record = new Record(client, workout, status);
        recordRepository.save(record);
        return record;
    }

    private Record updateStatus(RecordStatus status, Workout workout, User client, Record record) {
        switch (status) {
            case CANCELLED: {
                if (record.getStatus().equals(RecordStatus.ACTIVE)) {
                    List<Record> queueRecords = recordRepository.findAllByStatusAndWorkoutOrderByDateTime(RecordStatus.QUEUED, workout);
                    if (queueRecords.size() == 0) {
                        workout.setAvailableSeats(workout.getAvailableSeats() + 1);
                        workoutRepository.save(workout);
                    } else {
                        Record queueRecord = queueRecords.get(0);
                        queueRecord.setStatus(RecordStatus.ACTIVE);
                        recordRepository.save(queueRecord);
                    }
                }
                subscriptionService.increment(client);
                break;
            }
            case ACTIVE: {
                if (RecordStatus.QUEUED.equals(record.getStatus()) ||
                        RecordStatus.ACTIVE.equals(record.getStatus()) ||
                        RecordStatus.SKIPPED.equals(record.getStatus()) ||
                        RecordStatus.VISITED.equals(record.getStatus()) ||
                        record.getWorkout().getAvailableSeats() == 0
                ) {
                    throw new BadRequestException(BadRequestErrorCode.BAD_STATUS);
                }
                subscriptionService.countdown(client, record.getWorkout().getDate());
                workout.setAvailableSeats(workout.getAvailableSeats() - 1);
                workoutRepository.save(workout);
                break;
            }
            case QUEUED: {
                if (RecordStatus.ACTIVE.equals(record.getStatus())||
                        RecordStatus.QUEUED.equals(record.getStatus()) ||
                        RecordStatus.SKIPPED.equals(record.getStatus()) ||
                        RecordStatus.VISITED.equals(record.getStatus()) ||
                        record.getWorkout().getAvailableSeats() != 0
                ) {
                    throw new BadRequestException(BadRequestErrorCode.BAD_STATUS);
                }
                subscriptionService.countdown(client, record.getWorkout().getDate());
                break;
            }
        }

        record.setStatus(status);
        recordRepository.save(record);
        try {
            subscriptionService.getActiveSubscriptionByUser(client);
        } catch (AccessDeniedException e) {
            log.info("Subscribe deactivate");
        }
        return record;
    }
}

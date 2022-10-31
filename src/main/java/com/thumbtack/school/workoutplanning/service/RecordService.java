package com.thumbtack.school.workoutplanning.service;

import com.thumbtack.school.workoutplanning.exception.BadRequestErrorCode;
import com.thumbtack.school.workoutplanning.exception.BadRequestException;
import com.thumbtack.school.workoutplanning.model.AuthType;
import com.thumbtack.school.workoutplanning.model.Record;
import com.thumbtack.school.workoutplanning.model.StatusRecord;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@Transactional(rollbackOn = {Throwable.class})
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


    public List<Record> getRecordsByWorkout(Long id) {
        return workoutRepository.findById(id).orElseThrow(EntityNotFoundException::new).getRecords();
    }

    public Record insert(Long workoutId, String username, StatusRecord status) throws BadRequestException {
        if (AuthUtils.getRole().equals(AuthType.TRAINER)) {
            throw new AccessDeniedException("Access to insert record denied");
        }
        if (AuthUtils.getRole().equals(AuthType.CLIENT) && !AuthUtils.getUsername().equals(username)) {
            throw new BadRequestException(BadRequestErrorCode.INVALID_USERNAME, String.format("Username expected %s", AuthUtils.getUsername()));
        }
        Workout workout = workoutRepository.findById(workoutId).orElseThrow(EntityNotFoundException::new);
        LocalDateTime startWorkout = LocalDateTime.of(workout.getDate(), workout.getTimeStart());
        if (startWorkout.isBefore(LocalDateTime.now())) {
            throw new BadRequestException(BadRequestErrorCode.WORKOUT_ALREADY_STARTED);
        }

        if (workout.getAvailableSeats() == 0 && !StatusRecord.QUEUED.equals(status)) {
            throw new BadRequestException(BadRequestErrorCode.NOT_AVAILABLE_SEATS);
        }

        User client = userRepository.findByUsername(username);
        if (client == null || !client.getRole().getName().equals(AuthType.CLIENT)) {
            throw new BadRequestException(BadRequestErrorCode.CLIENT_NOT_FOUND);
        }

        if (StatusRecord.ACTIVE.equals(status)) {
            workout.setAvailableSeats(workout.getAvailableSeats() - 1);
        }
        workoutRepository.save(workout);
        Record record = new Record(client, workout, status);
        try {
            recordRepository.save(record);
            return record;
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException(BadRequestErrorCode.RECORD_ALREADY_EXIST);
        }
    }

    public List<Record> findAll() {
        return recordRepository.findAll();
    }

    public Record setStatus(Long workoutId, String username, StatusRecord status) throws BadRequestException {
        User client = userRepository.findByUsername(username);

        Optional<Workout> workoutOptional = workoutRepository.findById(workoutId);
        if (workoutOptional.isEmpty()) {
            throw new BadRequestException(BadRequestErrorCode.WORKOUT_NOT_FOUND);
        }
        Workout workout = workoutOptional.get();

        if (AuthUtils.getRole() == AuthType.CLIENT) {
            ruleService.checkRules(workout);
        }

        Record record = recordRepository.findByUserAndWorkout(client, workout);
        if (record == null) {
            throw new BadRequestException(BadRequestErrorCode.RECORD_NOT_FOUND);
        }

        if (record.getStatus().equals(StatusRecord.ACTIVE) && status.equals(StatusRecord.CANCELLED)) {
            List<Record> queueRecord = recordRepository.findAllByStatusOrderByDateTime(StatusRecord.QUEUED);
            if (queueRecord.size() == 0) {
                workout.setAvailableSeats(workout.getAvailableSeats() + 1);
                workoutRepository.save(workout);
            } else {
                queueRecord.get(0).setStatus(StatusRecord.ACTIVE);
                recordRepository.save(queueRecord.get(0));
            }
            record.setStatus(status);
            recordRepository.delete(record);
        } else {
            record.setStatus(status);
            if (status.equals(StatusRecord.CANCELLED)) {
                recordRepository.delete(record);
            } else {
                recordRepository.save(record);
            }
        }
        return record;
    }

    public StatusRecord getStatusByClient(Long workoutId) {
        if (AuthUtils.getRole().equals(AuthType.CLIENT)) {
            User client = userRepository.findByUsername(AuthUtils.getUsername());
            Optional<Workout> workout = workoutRepository.findById(workoutId);
            if (client == null || workout.isEmpty()) {
                return StatusRecord.UNDEFINED;
            }
            Record record = recordRepository.findByUserAndWorkout(client, workout.get());
            if (record == null) {
                return StatusRecord.UNDEFINED;
            }
            return record.getStatus();
        }
        return StatusRecord.UNDEFINED;
    }
}

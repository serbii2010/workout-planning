package com.thumbtack.school.workoutplanning.controller.workout;

import com.thumbtack.school.workoutplanning.dto.request.workout.GenerateWorkoutDtoRequest;
import com.thumbtack.school.workoutplanning.dto.request.workout.WorkoutDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.workout.RecordDtoResponse;
import com.thumbtack.school.workoutplanning.dto.response.workout.WorkoutDtoResponse;
import com.thumbtack.school.workoutplanning.exception.BadRequestException;
import com.thumbtack.school.workoutplanning.mappers.dto.RecordMapper;
import com.thumbtack.school.workoutplanning.mappers.dto.WorkoutMapper;
import com.thumbtack.school.workoutplanning.model.Record;
import com.thumbtack.school.workoutplanning.service.RecordService;
import com.thumbtack.school.workoutplanning.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/api/workouts")
public class WorkoutController {
    @Autowired
    private WorkoutService workoutService;
    @Autowired
    private RecordService recordService;

    @PreAuthorize(value = "isAuthenticated()")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<WorkoutDtoResponse> findWorkouts(
            @RequestParam(name = "trainer", required = false) String trainer,
            @RequestParam(name = "dateStart", required = false) @Valid
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateStart,
            @RequestParam(name = "dateEnd", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEnd,
            @RequestParam(name = "timeStart", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime timeStart,
            @RequestParam(name = "timeEnd", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime timeEnd
    ) throws BadRequestException, DateTimeParseException {
        return workoutService.filter(trainer, dateStart, dateEnd, timeStart, timeEnd)
                .stream()
                .map(workout -> WorkoutMapper.INSTANCE.workoutToDtoResponse(workout, recordService))
                .collect(Collectors.toList());
    }

    @PreAuthorize(value = "hasAnyRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name()," +
                        "T(com.thumbtack.school.workoutplanning.model.AuthType).TRAINER.name())")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public WorkoutDtoResponse findById(@PathVariable Long id) throws BadRequestException {
        return WorkoutMapper.INSTANCE.workoutToDtoResponse(workoutService.findById(id), recordService);
    }

    @PreAuthorize(value = "hasRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name())")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<WorkoutDtoResponse> generateWorkouts(@Valid @RequestBody GenerateWorkoutDtoRequest request) throws BadRequestException {
        return workoutService.generateWorkout(request).stream().map(workout -> WorkoutMapper.INSTANCE.workoutToDtoResponse(workout, recordService))
                .collect(Collectors.toList());
    }

    @PreAuthorize(value = "hasRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name())")
    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public WorkoutDtoResponse update(@PathVariable Long id, @Valid @RequestBody WorkoutDtoRequest request) throws BadRequestException {
        return WorkoutMapper.INSTANCE.workoutToDtoResponse(workoutService.update(id, request), recordService);
    }

    @PreAuthorize(value = "hasRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name())")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void delete(@PathVariable Long id) throws BadRequestException {
        workoutService.delete(id);
    }

    @PreAuthorize(value = "hasAnyRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name()," +
            "T(com.thumbtack.school.workoutplanning.model.AuthType).TRAINER.name())")
    @GetMapping(path = "/{id}/records", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<RecordDtoResponse> getRecords(@PathVariable Long id) {
        List<Record> records = recordService.getRecordsByWorkout(id);
        return records.stream().map(RecordMapper.INSTANCE::recordToDtoResponse).collect(Collectors.toList());
    }
}

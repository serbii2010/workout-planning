package com.thumbtack.school.workoutplanning.controller.workout;

import com.thumbtack.school.workoutplanning.dto.request.workout.RecordDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.workout.RecordDtoResponse;
import com.thumbtack.school.workoutplanning.exception.BadRequestException;
import com.thumbtack.school.workoutplanning.mappers.dto.RecordMapper;
import com.thumbtack.school.workoutplanning.model.Record;
import com.thumbtack.school.workoutplanning.model.StatusRecord;
import com.thumbtack.school.workoutplanning.service.RecordService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping(path = "/api/records")
public class RecordController {
    @Autowired
    private RecordService recordService;

    @PreAuthorize(value = "hasAnyRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name()," +
            "T(com.thumbtack.school.workoutplanning.model.AuthType).TRAINER.name())")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<RecordDtoResponse> findAll() {
        List<Record> records = recordService.findAll();
        return records.stream().map(RecordMapper.INSTANCE::recordToDtoResponse).collect(Collectors.toList());
    }

    @PreAuthorize(value = "hasAnyRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name()," +
            "T(com.thumbtack.school.workoutplanning.model.AuthType).CLIENT.name())")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public RecordDtoResponse insert(@Valid @RequestBody RecordDtoRequest request) throws BadRequestException {
        return RecordMapper.INSTANCE.recordToDtoResponse(recordService.insert(request.getWorkoutId(), request.getUsername(), StatusRecord.ACTIVE));
    }

    @PreAuthorize(value = "hasAnyRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name()," +
            "T(com.thumbtack.school.workoutplanning.model.AuthType).CLIENT.name())")
    @PostMapping(path = "/queue", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public RecordDtoResponse insertInQueue(@Valid @RequestBody RecordDtoRequest request) throws BadRequestException {
        return RecordMapper.INSTANCE.recordToDtoResponse(recordService.insert(request.getWorkoutId(), request.getUsername(), StatusRecord.QUEUED));
    }

    @PreAuthorize(value = "hasAnyRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name()," +
            "T(com.thumbtack.school.workoutplanning.model.AuthType).CLIENT.name())")
    @PutMapping(path = "/{status}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public RecordDtoResponse setStatus(@PathVariable StatusRecord status, @Valid @RequestBody RecordDtoRequest request) throws BadRequestException {
        return RecordMapper.INSTANCE.recordToDtoResponse(recordService.setStatus(request.getWorkoutId(), request.getUsername(), status));
    }
}

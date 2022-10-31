package com.thumbtack.school.workoutplanning.mappers.dto;

import com.thumbtack.school.workoutplanning.dto.response.workout.RecordDtoResponse;
import com.thumbtack.school.workoutplanning.model.Record;
import com.thumbtack.school.workoutplanning.utils.DateTimeUtils;
import java.time.LocalDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class, DateTimeUtils.class})
public interface RecordMapper {
    RecordMapper INSTANCE = Mappers.getMapper(RecordMapper.class);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "trainerUsername", source = "workout.trainer.username")
    @Mapping(target = "dateTimeWorkout",
            expression = "java(LocalDateTime.of(record.getWorkout().getDate(), record.getWorkout().getTimeStart()).format(DateTimeUtils.formatter))")
    @Mapping(target = "dateTimeRecord", expression = "java(record.getDateTime().format(DateTimeUtils.formatter))")
    @Mapping(target = "status", expression = "java(record.getStatus().name())")
    RecordDtoResponse recordToDtoResponse(Record record);
}

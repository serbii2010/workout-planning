package com.thumbtack.school.workoutplanning.mappers.dto;

import com.thumbtack.school.workoutplanning.dto.request.workout.GenerateWorkoutDtoRequest;
import com.thumbtack.school.workoutplanning.dto.request.workout.WorkoutDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.workout.WorkoutDtoResponse;
import com.thumbtack.school.workoutplanning.model.User;
import com.thumbtack.school.workoutplanning.model.Workout;
import java.time.LocalDate;

import com.thumbtack.school.workoutplanning.repository.UserRepository;
import com.thumbtack.school.workoutplanning.service.UserService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface WorkoutMapper {
    WorkoutMapper INSTANCE = Mappers.getMapper(WorkoutMapper.class);

    @Mapping(target = "trainer", source = "trainer.username")
    WorkoutDtoResponse workoutToDtoResponse(Workout workout);

    @Mapping(target = "date", source = "localDate")
    @Mapping(target = "availableSeats", source = "request.totalSeats")
    @Mapping(target = "trainer", source = "trainer")
    @Mapping(target = "id", expression = "java(Long.valueOf(0))")
    Workout workoutDtoRequestToWorkout(GenerateWorkoutDtoRequest request, LocalDate localDate, User trainer);

    @Mapping(target = "trainer", expression = "java(userService.findByUsername(request.getTrainer()))")
    void update(@MappingTarget Workout workout, WorkoutDtoRequest request, @Context UserService userService);
}

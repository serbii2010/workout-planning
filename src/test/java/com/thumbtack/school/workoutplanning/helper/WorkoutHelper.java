package com.thumbtack.school.workoutplanning.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thumbtack.school.workoutplanning.dto.request.workout.GenerateWorkoutDtoRequest;
import com.thumbtack.school.workoutplanning.dto.request.workout.WorkoutDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.workout.WorkoutDtoResponse;
import com.thumbtack.school.workoutplanning.model.RecordStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class WorkoutHelper {
    public static GenerateWorkoutDtoRequest getGenerateWorkoutDtoRequest() {
        return new GenerateWorkoutDtoRequest(
                "2022-09-01",
                "2022-10-01",
                "Sat",
                "trainer",
                "body",
                30,
                7,
                "12:30"
        );
    }

    public static GenerateWorkoutDtoRequest getGenerateWorkoutDtoRequestByCurrentWeek() {
        LocalDate currentDate = LocalDate.now();

        return new GenerateWorkoutDtoRequest(
                currentDate.toString(),
                currentDate.plusDays(6).toString(),
                "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31",
                "trainer",
                "body",
                30,
                1,
                "11:30"
        );
    }

    public static List<WorkoutDtoResponse> getGenerateWorkoutDtoResponses() {
        List<WorkoutDtoResponse> responses = new ArrayList<>();
        responses.add(new WorkoutDtoResponse(1, LocalDate.parse("2022-09-03"), "12:30:00",
                "trainer", 30, "body", 7, 7));
        responses.add(new WorkoutDtoResponse(2, LocalDate.parse("2022-09-10"), "12:30:00",
                "trainer", 30, "body", 7, 7));
        responses.add(new WorkoutDtoResponse(3, LocalDate.parse("2022-09-17"), "12:30:00",
                "trainer", 30, "body", 7, 7));
        responses.add(new WorkoutDtoResponse(4, LocalDate.parse("2022-09-24"), "12:30:00",
                "trainer", 30, "body", 7, 7));
        responses.add(new WorkoutDtoResponse(5, LocalDate.parse("2022-10-01"), "12:30:00",
                "trainer", 30, "body", 7, 7));
        return responses;
    }

    public static List<WorkoutDtoResponse> getListByFilter() {
        List<WorkoutDtoResponse> responses = new ArrayList<>();
        responses.add(new WorkoutDtoResponse(2, LocalDate.parse("2022-09-10"), "12:30:00",
                "trainer", 30, "body", 7, 7));
        responses.add(new WorkoutDtoResponse(3, LocalDate.parse("2022-09-17"), "12:30:00",
                "trainer", 30, "body", 7, 7));
        return responses;
    }

    public static List<WorkoutDtoResponse> getListByFilterRecordStatus() {
        List<WorkoutDtoResponse> responses = new ArrayList<>();
        responses.add(new WorkoutDtoResponse(2, LocalDate.parse("2022-09-10"), "12:30:00",
                "trainer", 30, "body", 7, 6, RecordStatus.ACTIVE.name()));
        return responses;
    }

    public static WorkoutDtoRequest getWorkoutUpdate() {
        return new WorkoutDtoRequest(LocalDate.parse("2022-09-12"), "12:35:00", "trainer", 40, "body2", 8, 6 );
    }

    public static WorkoutDtoResponse getWorkoutAfterUpdate() {
        return new WorkoutDtoResponse(2, LocalDate.parse("2022-09-12"), "12:35:00",
                "trainer", 40, "body2", 8, 6);
    }

    public static WorkoutDtoRequest getWorkoutUpdateDuplicate() {
        return new WorkoutDtoRequest(LocalDate.parse("2022-09-10"), "12:30:00",
                "trainer", 30, "body", 7, 7);
    }

    public static WorkoutDtoResponse getWorkout() {
        return new WorkoutDtoResponse(2, LocalDate.parse("2022-09-10"), "12:30:00",
                "trainer", 30, "body", 7, 7);
    }

    public static void generateWorkouts(MockMvc mvc, ObjectMapper mapper, Cookie cookie) throws Exception {
        GenerateWorkoutDtoRequest request = WorkoutHelper.getGenerateWorkoutDtoRequest();

        mvc.perform(post("/api/workouts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .cookie(cookie));
    }

    public static void generateWorkoutsCurrentWeek(MockMvc mvc, ObjectMapper mapper, Cookie cookie) throws Exception {
        GenerateWorkoutDtoRequest request = WorkoutHelper.getGenerateWorkoutDtoRequestByCurrentWeek();

        mvc.perform(post("/api/workouts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .cookie(cookie));
    }

    public static List<WorkoutDtoResponse> getWorkoutAfterDelete() {
        List<WorkoutDtoResponse> responses = new ArrayList<>();
        responses.add(new WorkoutDtoResponse(3, LocalDate.parse("2022-09-17"), "12:30:00",
                "trainer", 30, "body", 7, 7));
        return responses;
    }
}

package com.thumbtack.school.workoutplanning.controller.workout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thumbtack.school.workoutplanning.controller.GlobalErrorHandler;
import com.thumbtack.school.workoutplanning.dto.request.workout.GenerateWorkoutDtoRequest;
import com.thumbtack.school.workoutplanning.dto.request.workout.WorkoutDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.workout.RecordDtoResponse;
import com.thumbtack.school.workoutplanning.dto.response.workout.WorkoutDtoResponse;
import com.thumbtack.school.workoutplanning.exception.BadRequestErrorCode;
import com.thumbtack.school.workoutplanning.exception.InternalErrorCode;
import com.thumbtack.school.workoutplanning.helper.AccountHelper;
import com.thumbtack.school.workoutplanning.helper.ErrorHelper;
import com.thumbtack.school.workoutplanning.helper.RecordHelper;
import com.thumbtack.school.workoutplanning.helper.SubscribeHelper;
import com.thumbtack.school.workoutplanning.helper.WorkoutHelper;
import com.thumbtack.school.workoutplanning.model.RecordStatus;
import com.thumbtack.school.workoutplanning.service.DebugService;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.thumbtack.school.workoutplanning.security.jwt.JwtTokenProvider.JWT_TOKEN_NAME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource("classpath:test.properties")
@AutoConfigureMockMvc
class WorkoutControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private DebugService debugService;
    @Autowired
    private ObjectMapper mapper;

    private Cookie adminCookie;

    @BeforeEach
    public void init() throws Exception {
        debugService.clear();
        AccountHelper.registrationAdmin(mvc, mapper);
        adminCookie = AccountHelper.loginAdmin(mvc, mapper);
        AccountHelper.registrationTrainer(mvc, mapper, adminCookie);
        AccountHelper.registrationClient(mvc, mapper);
    }

    @Test
    void generateWorkoutsTest() throws Exception {
        GenerateWorkoutDtoRequest request = WorkoutHelper.getGenerateWorkoutDtoRequest();
        List<WorkoutDtoResponse> response = WorkoutHelper.getGenerateWorkoutDtoResponses();

        mvc.perform(post("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void generateWorkoutsTest_badTrainer() throws Exception {
        GenerateWorkoutDtoRequest request = WorkoutHelper.getGenerateWorkoutDtoRequest();
        request.setTrainer("client21");
        GlobalErrorHandler.MyError response = ErrorHelper.getBadRequestDtoResponse(BadRequestErrorCode.TRAINER_NOT_FOUND);

        mvc.perform(post("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .cookie(adminCookie))
                .andExpect(status().isBadRequest())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void generateWorkoutsTest_duplicate() throws Exception {
        WorkoutHelper.generateWorkouts(mvc, mapper, adminCookie);

        GenerateWorkoutDtoRequest request = WorkoutHelper.getGenerateWorkoutDtoRequest();
        BadRequestErrorCode error = BadRequestErrorCode.DATETIME_WORKOUT_ALREADY_EXISTS;
        error.setErrorString("Workout by date [2022-09-03, 2022-09-10, 2022-09-17, 2022-09-24, 2022-10-01] already exist in time [12:30-13:00].");
        GlobalErrorHandler.MyError response = ErrorHelper.getBadRequestDtoResponse(error);

        mvc.perform(post("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .cookie(adminCookie))
                .andExpect(status().isBadRequest())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void findWorkoutsTest() throws Exception {
        WorkoutHelper.generateWorkouts(mvc, mapper, adminCookie);
        List<WorkoutDtoResponse> response = WorkoutHelper.getListByFilter();

        mvc.perform(get("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("trainer", "trainer")
                        .param("dateStart", "2022-09-10")
                        .param("dateEnd", "2022-09-19")
                        .param("timeStart", "11:30")
                        .param("timeEnd", "13:30")
                        .cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void findWorkoutsTest_byClient() throws Exception {
        WorkoutHelper.generateWorkouts(mvc, mapper, adminCookie);
        List<WorkoutDtoResponse> response = WorkoutHelper.getListByFilter();
        Cookie cookie = AccountHelper.loginClient(mvc, mapper);

        mvc.perform(get("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("trainer", "trainer")
                        .param("dateStart", "2022-09-10")
                        .param("dateEnd", "2022-09-19")
                        .param("timeStart", "11:30")
                        .param("timeEnd", "13:30")
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void findWorkoutsTest_byRecordStatus() throws Exception {
        WorkoutHelper.generateWorkouts(mvc, mapper, adminCookie);
        SubscribeHelper.insertSubscribeUnlimited(mvc, mapper, adminCookie);
        SubscribeHelper.activatedSubscription(1, mvc, mapper, adminCookie);

        List<WorkoutDtoResponse> response = WorkoutHelper.getListByFilterRecordStatus();
        Cookie cookie = AccountHelper.loginClient(mvc, mapper);
        RecordHelper.insertRecordByClient(mvc, mapper, adminCookie);

        mvc.perform(get("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("recordStatus", RecordStatus.ACTIVE.name())
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void findWorkoutsTest_byRecordStatusByAdmin() throws Exception {
        GlobalErrorHandler.MyError response = ErrorHelper.getBadRequestDtoResponse(InternalErrorCode.FORBIDDEN);

        mvc.perform(get("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("recordStatus", RecordStatus.ACTIVE.name())
                        .cookie(adminCookie))
                .andExpect(status().isForbidden())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void findWorkoutsTest_trainerNotFound() throws Exception {
        WorkoutHelper.generateWorkouts(mvc, mapper, adminCookie);
        GlobalErrorHandler.MyError response = ErrorHelper.getBadRequestDtoResponse(BadRequestErrorCode.TRAINER_NOT_FOUND);

        mvc.perform(get("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("trainer", "badTrainer")
                        .param("dateStart", "2022-09-10")
                        .param("dateEnd", "2022-09-19")
                        .param("timeStart", "11:30")
                        .param("timeEnd", "13:30")
                        .cookie(adminCookie))
                .andExpect(status().isBadRequest())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void updateTest() throws Exception {
        WorkoutHelper.generateWorkouts(mvc, mapper, adminCookie);
        WorkoutDtoRequest request = WorkoutHelper.getWorkoutUpdate();
        WorkoutDtoResponse response = WorkoutHelper.getWorkoutAfterUpdate();

        mvc.perform(put("/api/workouts/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(request))
                        .cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void updateTest_notFound() throws Exception {
        WorkoutHelper.generateWorkouts(mvc, mapper, adminCookie);
        WorkoutDtoRequest request = WorkoutHelper.getWorkoutUpdate();
        GlobalErrorHandler.MyError response = ErrorHelper.getBadRequestDtoResponse(BadRequestErrorCode.WORKOUT_NOT_FOUND);

        mvc.perform(put("/api/workouts/20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(request))
                        .cookie(adminCookie))
                .andExpect(status().isBadRequest())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void updateTest_duplicate() throws Exception {
        WorkoutHelper.generateWorkouts(mvc, mapper, adminCookie);
        WorkoutDtoRequest request = WorkoutHelper.getWorkoutUpdateDuplicate();
        BadRequestErrorCode error = BadRequestErrorCode.DATETIME_WORKOUT_ALREADY_EXISTS;
        error.setErrorString("Workout by date [2022-09-10] already exist in time [12:30:00-13:00].");
        GlobalErrorHandler.MyError response = ErrorHelper.getBadRequestDtoResponse(error);

        mvc.perform(put("/api/workouts/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(request))
                        .cookie(adminCookie))
                .andExpect(status().isBadRequest())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void deleteTest() throws Exception {
        WorkoutHelper.generateWorkouts(mvc, mapper, adminCookie);

        List<WorkoutDtoResponse> response = WorkoutHelper.getWorkoutAfterDelete();

        mvc.perform(delete("/api/workouts/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME));

        mvc.perform(get("/api/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("trainer", "trainer")
                        .param("dateStart", "2022-09-10")
                        .param("dateEnd", "2022-09-19")
                        .param("timeStart", "11:30")
                        .param("timeEnd", "13:30")
                        .cookie(adminCookie))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void findById() throws Exception {
        WorkoutHelper.generateWorkouts(mvc, mapper, adminCookie);
        WorkoutDtoResponse response = WorkoutHelper.getWorkout();

        mvc.perform(get("/api/workouts/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void getRecords() throws Exception {
        WorkoutHelper.generateWorkoutsCurrentWeek(mvc, mapper, adminCookie);
        SubscribeHelper.insertSubscribeUnlimited(mvc, mapper, adminCookie);
        SubscribeHelper.activatedSubscription(1, mvc, mapper, adminCookie);
        RecordHelper.insertRecordByClient(mvc, mapper, adminCookie);
        List<RecordDtoResponse> response = Collections.singletonList(RecordHelper.getRecordDtoResponse());

        mvc.perform(get("/api/workouts/2/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }
}
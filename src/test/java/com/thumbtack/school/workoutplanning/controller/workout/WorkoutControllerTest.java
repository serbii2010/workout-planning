package com.thumbtack.school.workoutplanning.controller.workout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thumbtack.school.workoutplanning.dto.request.workout.GenerateWorkoutDtoRequest;
import com.thumbtack.school.workoutplanning.dto.request.workout.WorkoutDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.workout.WorkoutDtoResponse;
import com.thumbtack.school.workoutplanning.helper.AccountHelper;
import com.thumbtack.school.workoutplanning.helper.WorkoutHelper;
import com.thumbtack.school.workoutplanning.service.DebugService;
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

import javax.servlet.http.Cookie;
import java.util.List;

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
}
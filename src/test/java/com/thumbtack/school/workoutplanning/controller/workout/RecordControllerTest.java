package com.thumbtack.school.workoutplanning.controller.workout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thumbtack.school.workoutplanning.dto.request.options.OptionDtoRequest;
import com.thumbtack.school.workoutplanning.dto.request.workout.RecordDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.option.OptionDtoResponse;
import com.thumbtack.school.workoutplanning.dto.response.workout.RecordDtoResponse;
import com.thumbtack.school.workoutplanning.helper.AccountHelper;
import com.thumbtack.school.workoutplanning.helper.OptionHelper;
import com.thumbtack.school.workoutplanning.helper.RecordHelper;
import com.thumbtack.school.workoutplanning.helper.WorkoutHelper;
import com.thumbtack.school.workoutplanning.model.RecordStatus;
import com.thumbtack.school.workoutplanning.service.DebugService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.thumbtack.school.workoutplanning.security.jwt.JwtTokenProvider.JWT_TOKEN_NAME;
import static org.mockito.Mockito.mockStatic;
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
class RecordControllerTest {
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
        WorkoutHelper.generateWorkoutsCurrentWeek(mvc, mapper, adminCookie);
    }

    @Test
    void findAll_emptyByAdmin() throws Exception {
        List<RecordDtoResponse> response = new ArrayList<>();

        mvc.perform(get("/api/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void findAll_byAdmin() throws Exception {
        RecordHelper.insertRecordByClient(mvc, mapper, adminCookie);
        List<RecordDtoResponse> responses = new ArrayList<>();
        responses.add(RecordHelper.getRecordDtoResponse());

        mvc.perform(get("/api/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(responses)));
    }

    @Test
    void insert() throws Exception {
        RecordDtoRequest request = RecordHelper.getRecordDtoRequest();
        RecordDtoResponse response = RecordHelper.getRecordDtoResponse();

        mvc.perform(post("/api/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void insertInQueue() throws Exception {
        AccountHelper.registrationClientTwo(mvc, mapper);
        RecordHelper.insertRecordByClient(mvc, mapper, adminCookie);

        RecordDtoRequest request = RecordHelper.getRecordTwoDtoRequest();
        RecordDtoResponse response = RecordHelper.getRecordQueueDtoResponse();

        mvc.perform(post("/api/records/queue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void cancel() throws Exception {
        RecordHelper.insertRecordByClient(mvc, mapper, adminCookie);
        RecordDtoRequest request = RecordHelper.getRecordDtoRequest();
        RecordDtoResponse response = RecordHelper.getRecordCancelDtoResponse();
        mvc.perform(put("/api/records/"+ RecordStatus.CANCELLED.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }


    @Test
    void cancel_withQueueClient() throws Exception {
        AccountHelper.registrationClientTwo(mvc, mapper);
        RecordHelper.insertRecordByClient(mvc, mapper, adminCookie);
        RecordHelper.insertRecordByClient2InQueue(mvc, mapper, adminCookie);
        RecordHelper.cancelRecordClient1(mvc, mapper, adminCookie);

        List<RecordDtoResponse> responses = new ArrayList<>();
        responses.add(RecordHelper.getRecordTwoActiveDtoResponse());
        responses.add(RecordHelper.getRecordCancelDtoResponse());

        mvc.perform(get("/api/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(responses)));
    }

    @Test
    void skip() throws Exception {
        RecordHelper.insertRecordByClient(mvc, mapper, adminCookie);
        RecordDtoRequest request = RecordHelper.getRecordDtoRequest();
        RecordDtoResponse response = RecordHelper.getRecordSkipDtoResponse();
        mvc.perform(put("/api/records/"+ RecordStatus.SKIPPED.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void getRule() throws Exception {
        OptionHelper.setOptionHourBeforeWorkout(mvc, mapper, adminCookie);
        OptionDtoResponse response = OptionHelper.getOptionHourDtoResponse();

        mvc.perform(get("/api/rules/HOUR_BEFORE_WORKOUT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void getRule_beforeMorning() throws Exception {
        OptionHelper.setOptionBeforeMorningWorkout(mvc, mapper, adminCookie);
        OptionDtoResponse response = OptionHelper.getOptionBeforeMorningDtoResponse();

        mvc.perform(get("/api/rules/TIME_BEFORE_MORNING_WORKOUT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void setRule() throws Exception {
        OptionDtoRequest request = OptionHelper.getOptionDtoRequest();
        OptionDtoResponse response = OptionHelper.getOptionHourDtoResponse();

        mvc.perform(post("/api/rules/HOUR_BEFORE_WORKOUT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void setRule_beforeMorning() throws Exception {
        OptionDtoRequest request = OptionHelper.getOptionBeforeMorningDtoRequest();
        OptionDtoResponse response = OptionHelper.getOptionBeforeMorningDtoResponse();

        mvc.perform(post("/api/rules/TIME_BEFORE_MORNING_WORKOUT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void cancel_thresholdBeforeMorning() throws Exception {
        OptionHelper.setOptionBeforeMorningWorkout(mvc, mapper, adminCookie);
        RecordHelper.insertRecordByClient(mvc, mapper, adminCookie);
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse("18:59:59"));
        RecordDtoRequest request = RecordHelper.getRecordDtoRequest();
        RecordDtoResponse response = RecordHelper.getRecordCancelDtoResponse();

        try(MockedStatic<LocalDateTime> dt = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            dt.when(LocalDateTime::now).thenReturn(dateTime);

            mvc.perform(put("/api/records/"+ RecordStatus.CANCELLED.name())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request))
                            .cookie(adminCookie))
                    .andExpect(status().isOk())
                    .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        }
    }

    @Test
    void insert_badThresholdBeforeMorning() throws Exception {
        OptionHelper.setOptionBeforeMorningWorkout(mvc, mapper, adminCookie);
        RecordHelper.insertRecordByClient(mvc, mapper, adminCookie);
        Cookie cookie = AccountHelper.loginClient(mvc, mapper);
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse("19:00:01"));
        RecordDtoRequest request = RecordHelper.getRecordDtoRequest();

        try(MockedStatic<LocalDateTime> dt = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            dt.when(LocalDateTime::now).thenReturn(dateTime);

            mvc.perform(put("/api/records/"+ RecordStatus.CANCELLED.name())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request))
                            .cookie(cookie))
                    .andExpect(status().isBadRequest())
                    .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME));
        }
    }

    @Test
    void cancel_badThresholdBeforeHour() throws Exception {
        OptionHelper.setOptionHourBeforeWorkout(mvc, mapper, adminCookie);
        RecordHelper.insertRecordByClient(mvc, mapper, adminCookie);
        Cookie cookie = AccountHelper.loginClient(mvc, mapper);
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse("11:25:01"));
        RecordDtoRequest request = RecordHelper.getRecordDtoRequestNow();

        try(MockedStatic<LocalDateTime> dt = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            dt.when(LocalDateTime::now).thenReturn(dateTime);

            mvc.perform(put("/api/records/"+ RecordStatus.CANCELLED.name())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request))
                            .cookie(cookie))
                    .andExpect(status().isBadRequest())
                    .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME));
        }
    }
}
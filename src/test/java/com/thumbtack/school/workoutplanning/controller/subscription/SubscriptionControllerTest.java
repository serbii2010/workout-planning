package com.thumbtack.school.workoutplanning.controller.subscription;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thumbtack.school.workoutplanning.dto.request.subscription.SubscriptionActivateDtoRequest;
import com.thumbtack.school.workoutplanning.dto.request.subscription.SubscriptionUnlimitedDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.subscription.SubscriptionLimitedDtoResponse;
import com.thumbtack.school.workoutplanning.dto.response.subscription.SubscriptionUnlimitedDtoResponse;
import com.thumbtack.school.workoutplanning.helper.AccountHelper;
import com.thumbtack.school.workoutplanning.helper.RecordHelper;
import com.thumbtack.school.workoutplanning.helper.SubscribeHelper;
import com.thumbtack.school.workoutplanning.helper.WorkoutHelper;
import com.thumbtack.school.workoutplanning.model.SubscriptionType;
import com.thumbtack.school.workoutplanning.service.DebugService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
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
class SubscriptionControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private DebugService debugService;
    @Autowired
    private ObjectMapper mapper;

    private Cookie adminCookie;
    private Cookie clientCookie;

    @BeforeEach
    public void init() throws Exception {
        debugService.clear();
        AccountHelper.registrationAdmin(mvc, mapper);
        adminCookie = AccountHelper.loginAdmin(mvc, mapper);
        AccountHelper.registrationTrainer(mvc, mapper, adminCookie);
        AccountHelper.registrationClient(mvc, mapper);
        clientCookie = AccountHelper.loginClient(mvc, mapper);
        WorkoutHelper.generateWorkoutsCurrentWeek(mvc, mapper, adminCookie);
    }

    @Test
    void insert() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse("18:59:59"));
        try(MockedStatic<LocalDateTime> dt = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            dt.when(LocalDateTime::now).thenReturn(dateTime);
            SubscriptionUnlimitedDtoRequest request = SubscribeHelper.getSubscribeUnlimitedDtoRequest();
            SubscriptionUnlimitedDtoResponse response = SubscribeHelper.getSubscribeUnlimitedDtoResponse();

            mvc.perform(post("/api/subscriptions")
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
    void filter_unlimited() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse("18:59:59"));
        try(MockedStatic<LocalDateTime> dt = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            dt.when(LocalDateTime::now).thenReturn(dateTime);
            SubscribeHelper.insertSubscribeUnlimited(mvc, mapper, adminCookie);
            SubscribeHelper.insertSubscribeLimited(mvc, mapper, adminCookie);

            List<SubscriptionUnlimitedDtoResponse> response = Collections.singletonList(SubscribeHelper.getSubscribeUnlimitedDtoResponse());
            mvc.perform(get("/api/subscriptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .param("type", SubscriptionType.UNLIMITED_VALUE)
                            .cookie(adminCookie))
                    .andExpect(status().isOk())
                    .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        }
    }

    @Test
    void filter_limited() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse("18:59:59"));
        try(MockedStatic<LocalDateTime> dt = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            dt.when(LocalDateTime::now).thenReturn(dateTime);
            SubscribeHelper.insertSubscribeLimited(mvc, mapper, adminCookie);
            SubscribeHelper.insertSubscribeUnlimited(mvc, mapper, adminCookie);

            List<SubscriptionLimitedDtoResponse> response = Collections.singletonList(SubscribeHelper.getSubscribeLimitedDtoResponse());
            mvc.perform(get("/api/subscriptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .param("type", SubscriptionType.LIMITED_VALUE)
                            .cookie(adminCookie))
                    .andExpect(status().isOk())
                    .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        }
    }

    @Test
    void filter_limitByClient() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse("18:59:59"));
        try(MockedStatic<LocalDateTime> dt = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            dt.when(LocalDateTime::now).thenReturn(dateTime);
            SubscribeHelper.insertSubscribeLimited(mvc, mapper, adminCookie);
            SubscribeHelper.insertSubscribeUnlimited(mvc, mapper, adminCookie);

            List<SubscriptionLimitedDtoResponse> response = Collections.singletonList(SubscribeHelper.getSubscribeLimitedDtoResponse());
            mvc.perform(get("/api/subscriptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .param("type", SubscriptionType.LIMITED_VALUE)
                            .cookie(clientCookie))
                    .andExpect(status().isOk())
                    .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        }
    }

    @Test
    void filter_limitMinusOneByClient() throws Exception {
        WorkoutHelper.generateWorkouts(mvc, mapper, adminCookie);
        RecordHelper.insertRecordByClient(mvc, mapper, clientCookie);

        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse("18:59:59"));
        try(MockedStatic<LocalDateTime> dt = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            dt.when(LocalDateTime::now).thenReturn(dateTime);

            SubscribeHelper.insertSubscribeLimited(mvc, mapper, adminCookie);
            SubscribeHelper.insertSubscribeUnlimited(mvc, mapper, adminCookie);
            SubscribeHelper.activatedSubscription(1, mvc, mapper, adminCookie);

            List<SubscriptionLimitedDtoResponse> response = Collections.singletonList(SubscribeHelper.getSubscribeLimitedDtoResponseMinusOne());
            mvc.perform(get("/api/subscriptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .param("type", SubscriptionType.LIMITED_VALUE)
                            .cookie(clientCookie))
                    .andExpect(status().isOk())
                    .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        }
    }

    @Test
    void filter_limitByClientAfterCancel() throws Exception {
        WorkoutHelper.generateWorkouts(mvc, mapper, adminCookie);
        RecordHelper.insertRecordByClient(mvc, mapper, clientCookie);
        RecordHelper.cancelRecordClient1(mvc, mapper, clientCookie);

        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse("18:59:59"));
        try(MockedStatic<LocalDateTime> dt = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            dt.when(LocalDateTime::now).thenReturn(dateTime);

            SubscribeHelper.insertSubscribeLimited(mvc, mapper, adminCookie);
            SubscribeHelper.insertSubscribeUnlimited(mvc, mapper, adminCookie);
            SubscribeHelper.activatedSubscription(1, mvc, mapper, adminCookie);
            List<SubscriptionLimitedDtoResponse> response = Collections.singletonList(SubscribeHelper.getActivatedSubscribeLimitedDtoResponse());

            mvc.perform(get("/api/subscriptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .param("type", SubscriptionType.LIMITED_VALUE)
                            .cookie(clientCookie))
                    .andExpect(status().isOk())
                    .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        }
    }

    @Test
    void activate_deactivate() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse("18:59:59"));
        try(MockedStatic<LocalDateTime> dt = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            dt.when(LocalDateTime::now).thenReturn(dateTime);
            SubscribeHelper.insertSubscribeUnlimited(mvc, mapper, adminCookie);
            SubscriptionActivateDtoRequest request = new SubscriptionActivateDtoRequest(false);
            SubscriptionUnlimitedDtoResponse response = SubscribeHelper.getSubscribeUnlimitedDeactivateDtoResponse();

            mvc.perform(put("/api/subscriptions/1")
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
    void activate_activate() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse("18:59:59"));
        try(MockedStatic<LocalDateTime> dt = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            dt.when(LocalDateTime::now).thenReturn(dateTime);
            SubscribeHelper.insertSubscribeUnlimited(mvc, mapper, adminCookie);

            SubscriptionActivateDtoRequest request = new SubscriptionActivateDtoRequest(true);
            SubscriptionUnlimitedDtoResponse response = SubscribeHelper.getActivatedSubscribeUnlimitedDtoResponse();

            mvc.perform(put("/api/subscriptions/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request))
                            .cookie(adminCookie))
                    .andExpect(status().isOk())
                    .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        }
    }
}
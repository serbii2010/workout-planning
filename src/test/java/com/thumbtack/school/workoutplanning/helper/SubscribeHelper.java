package com.thumbtack.school.workoutplanning.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thumbtack.school.workoutplanning.dto.request.subscription.SubscriptionActivateDtoRequest;
import com.thumbtack.school.workoutplanning.dto.request.subscription.SubscriptionLimitedDtoRequest;
import com.thumbtack.school.workoutplanning.dto.request.subscription.SubscriptionUnlimitedDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.subscription.SubscriptionLimitedDtoResponse;
import com.thumbtack.school.workoutplanning.dto.response.subscription.SubscriptionUnlimitedDtoResponse;
import com.thumbtack.school.workoutplanning.model.SubscriptionType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class SubscribeHelper {
    public  static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static SubscriptionUnlimitedDtoRequest getSubscribeUnlimitedDtoRequest() {
        return new SubscriptionUnlimitedDtoRequest(
                "client",
                LocalDate.now(),
                3
        );
    }

    public static SubscriptionUnlimitedDtoResponse getSubscribeUnlimitedDtoResponse() {
        return new SubscriptionUnlimitedDtoResponse(
                1L,
                "client",
                false,
                LocalDateTime.now().format(formatter),
                SubscriptionType.UNLIMITED_VALUE,
                LocalDate.now(),
                LocalDate.now().plusMonths(3)
        );
    }

        public static SubscriptionUnlimitedDtoResponse getActivatedSubscribeUnlimitedDtoResponse() {
        return new SubscriptionUnlimitedDtoResponse(
                1L,
                "client",
                true,
                LocalDateTime.now().format(formatter),
                SubscriptionType.UNLIMITED_VALUE,
                LocalDate.now(),
                LocalDate.now().plusMonths(3)
        );
    }

    public static SubscriptionLimitedDtoRequest getSubscribeLimitedDtoRequest() {
        return new SubscriptionLimitedDtoRequest(
                "client",
                8
        );
    }

    public static SubscriptionLimitedDtoResponse getSubscribeLimitedDtoResponse() {
        return new SubscriptionLimitedDtoResponse(
                1L,
                "client",
                false,
                LocalDateTime.now().format(formatter),
                SubscriptionType.LIMITED_VALUE,
                8,
                8
        );
    }

    public static SubscriptionLimitedDtoResponse getActivatedSubscribeLimitedDtoResponse() {
        return new SubscriptionLimitedDtoResponse(
                1L,
                "client",
                true,
                LocalDateTime.now().format(formatter),
                SubscriptionType.LIMITED_VALUE,
                8,
                8
        );
    }

    public static SubscriptionLimitedDtoResponse getSubscribeLimitedDtoResponseMinusOne() {
        return new SubscriptionLimitedDtoResponse(
                1L,
                "client",
                true,
                LocalDateTime.now().format(formatter),
                SubscriptionType.LIMITED_VALUE,
                8,
                8
        );
    }

    public static void insertSubscribeUnlimited(MockMvc mvc, ObjectMapper mapper, Cookie cookie) throws Exception {
        SubscriptionUnlimitedDtoRequest request = SubscribeHelper.getSubscribeUnlimitedDtoRequest();

        mvc.perform(post("/api/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .cookie(cookie));
    }

    public static void insertSubscribeUnlimitedByClientTwo(MockMvc mvc, ObjectMapper mapper, Cookie adminCookie) throws Exception {
        SubscriptionUnlimitedDtoRequest request = SubscribeHelper.getSubscribeUnlimitedDtoRequest();
        request.setUsername("client2");

        mvc.perform(post("/api/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .cookie(adminCookie));
    }

    public static void insertSubscribeLimited(MockMvc mvc, ObjectMapper mapper, Cookie cookie) throws Exception {
        SubscriptionLimitedDtoRequest request = SubscribeHelper.getSubscribeLimitedDtoRequest();

        mvc.perform(post("/api/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .cookie(cookie));
    }

    public static SubscriptionUnlimitedDtoResponse getSubscribeUnlimitedDeactivateDtoResponse() {
        SubscriptionUnlimitedDtoResponse response = SubscribeHelper.getSubscribeUnlimitedDtoResponse();
        response.setActive(false);
        return response;
    }

    public static void activatedSubscription(int id, MockMvc mvc, ObjectMapper mapper, Cookie cookie) throws Exception {
        SubscriptionActivateDtoRequest request = new SubscriptionActivateDtoRequest(true);

        mvc.perform(put("/api/subscriptions/"+ id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .cookie(cookie));
    }
}

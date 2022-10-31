package com.thumbtack.school.workoutplanning.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thumbtack.school.workoutplanning.dto.request.account.AuthDtoRequest;
import com.thumbtack.school.workoutplanning.dto.request.account.RegistrationDtoRequest;
import com.thumbtack.school.workoutplanning.dto.request.account.UpdateAccountDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.account.AuthDtoResponse;
import com.thumbtack.school.workoutplanning.model.AuthType;
import javax.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.thumbtack.school.workoutplanning.security.jwt.JwtTokenProvider.JWT_TOKEN_NAME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class AccountHelper {
    public static RegistrationDtoRequest getAdminRegistrationDroRequest() {
        return new RegistrationDtoRequest(
                "admin",
                "1234",
                "adminFirst",
                "adminLast",
                "admin@workoutplanning.com",
                "8800-555-3535"
        );
    }

    public static RegistrationDtoRequest getTrainerRegistrationDroRequest() {
        return new RegistrationDtoRequest(
                "trainer",
                "1234",
                "tr",
                "tra",
                "trainer@workoutplanning.com",
                "8800-555-3535"
        );
    }

    public static AuthDtoResponse getAdminAuthDtoResponse() {
        return new AuthDtoResponse(
                "admin",
                "adminFirst",
                "adminLast",
                "admin@workoutplanning.com",
                "8800-555-3535",
                AuthType.ADMIN.name()
        );
    }

    public static AuthDtoResponse getTrainerAuthDtoResponse() {
        return new AuthDtoResponse(
                "trainer",
                "tr",
                "tra",
                "trainer@workoutplanning.com",
                "8800-555-3535",
                AuthType.TRAINER.name()
        );
    }

    public static RegistrationDtoRequest getClientRegistrationDtoRequest() {
        return new RegistrationDtoRequest(
                "client",
                "1234",
                "clientFirst",
                "clientLast",
                "client@workoutplanning.com",
                "8800-555-3535"
        );
    }

    public static AuthDtoResponse getClientAuthDtoResponse() {
        return new AuthDtoResponse(
                "client",
                "clientFirst",
                "clientLast",
                "client@workoutplanning.com",
                "8800-555-3535",
                AuthType.CLIENT.name()
        );
    }

    public static AuthDtoRequest getAuthAdminDtoRequest() {
        return new AuthDtoRequest(
                "admin",
                "1234"
        );
    }

    private static AuthDtoRequest getAuthTrainerDtoRequest() {
        return new AuthDtoRequest(
                "trainer",
                "1234"
        );
    }

    public static AuthDtoRequest getAuthClientDtoRequest() {
        return new AuthDtoRequest(
                "client",
                "1234"
        );
    }

    public static UpdateAccountDtoRequest getTrainerUpdateDtoRequest() {
        return new UpdateAccountDtoRequest(
                "trainer-f",
                "tr",
                "trainer1@workout.ru",
                "8800-555-35-36"
        );
    }

    public static AuthDtoResponse getAuthAfterUpdateDtoResponse() {
        return new AuthDtoResponse(
                "trainer",
                "trainer-f",
                "tr",
                "trainer1@workout.ru",
                "8800-555-35-36",
                "TRAINER"
        );
    }

    public static void registrationAdmin(MockMvc mvc, ObjectMapper mapper) throws Exception {
        RegistrationDtoRequest request = AccountHelper.getAdminRegistrationDroRequest();

        mvc.perform(post("/api/admin-accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));
    }


    public static void registrationTrainer(MockMvc mvc, ObjectMapper mapper, Cookie cookie) throws Exception {
        RegistrationDtoRequest request = AccountHelper.getTrainerRegistrationDroRequest();

        mvc.perform(post("/api/trainer-accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .content(mapper.writeValueAsString(request)));
    }

    public static void registrationClient(MockMvc mvc, ObjectMapper mapper) throws Exception {
        RegistrationDtoRequest request = AccountHelper.getClientRegistrationDtoRequest();

        mvc.perform(post("/api/client-accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));
    }

    public static void registrationClientTwo(MockMvc mvc, ObjectMapper mapper) throws Exception {
        RegistrationDtoRequest request = AccountHelper.getClientRegistrationDtoRequest();
        request.setUsername("client2");
        request.setEmail("client2@workoutplanning.com");

        mvc.perform(post("/api/client-accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));
    }

    public static Cookie loginAdmin(MockMvc mvc, ObjectMapper mapper) throws Exception {
        AuthDtoRequest request = AccountHelper.getAuthAdminDtoRequest();

        return mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getCookie(JWT_TOKEN_NAME);

    }

    public static Cookie loginTrainer(MockMvc mvc, ObjectMapper mapper) throws Exception {
        AuthDtoRequest request = AccountHelper.getAuthTrainerDtoRequest();

        return mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getCookie(JWT_TOKEN_NAME);
    }

    public static Cookie loginClient(MockMvc mvc, ObjectMapper mapper) throws Exception {
        AuthDtoRequest request = AccountHelper.getAuthClientDtoRequest();

        return mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getCookie(JWT_TOKEN_NAME);

    }

}

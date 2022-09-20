package com.thumbtack.school.workoutplanning.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thumbtack.school.workoutplanning.dto.request.AuthDtoRequest;
import com.thumbtack.school.workoutplanning.dto.request.RegistrationDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.auth.AuthDtoResponse;
import com.thumbtack.school.workoutplanning.model.AuthType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

    public static AuthDtoRequest getAuthClientDtoRequest() {
        return new AuthDtoRequest(
                "client",
                "1234"
        );
    }

    public static void registrationAdmin(MockMvc mvc, ObjectMapper mapper) throws Exception {
        RegistrationDtoRequest request = AccountHelper.getAdminRegistrationDroRequest();

        mvc.perform(post("/api/admin/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));
    }

    public static void registrationClient(MockMvc mvc, ObjectMapper mapper) throws Exception {
        RegistrationDtoRequest request = AccountHelper.getClientRegistrationDtoRequest();

        mvc.perform(post("/api/client/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));
    }
}

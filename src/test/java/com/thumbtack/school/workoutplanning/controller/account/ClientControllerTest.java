package com.thumbtack.school.workoutplanning.controller.account;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.thumbtack.school.workoutplanning.controller.GlobalErrorHandler;
import com.thumbtack.school.workoutplanning.dto.request.account.RegistrationDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.account.AuthDtoResponse;
import com.thumbtack.school.workoutplanning.exception.BadRequestErrorCode;
import com.thumbtack.school.workoutplanning.helper.AccountHelper;
import com.thumbtack.school.workoutplanning.helper.ErrorHelper;
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

import static com.thumbtack.school.workoutplanning.security.jwt.JwtTokenProvider.JWT_TOKEN_NAME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource("classpath:test.properties")
@AutoConfigureMockMvc
class ClientControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private DebugService debugService;
    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void init() {
        debugService.clear();
    }

    @Test
    void registration() throws Exception {
        RegistrationDtoRequest request = AccountHelper.getClientRegistrationDtoRequest();
        AuthDtoResponse response = AccountHelper.getClientAuthDtoResponse();

        mvc.perform(post("/api/client-accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }


    @Test
    void registration_duplicateUsername() throws Exception {
        AccountHelper.registrationClient(mvc, mapper);

        RegistrationDtoRequest request = AccountHelper.getClientRegistrationDtoRequest();
        GlobalErrorHandler.MyError response = ErrorHelper.getBadRequestDtoResponse(BadRequestErrorCode.USERNAME_ALREADY_USED);

        mvc.perform(post("/api/client-accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME));
    }
}
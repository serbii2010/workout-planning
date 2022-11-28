package com.thumbtack.school.workoutplanning.controller.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thumbtack.school.workoutplanning.controller.GlobalErrorHandler;
import com.thumbtack.school.workoutplanning.dto.request.account.AuthDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.account.AuthDtoResponse;
import com.thumbtack.school.workoutplanning.dto.response.account.RoleDtoResponse;
import com.thumbtack.school.workoutplanning.exception.BadRequestErrorCode;
import com.thumbtack.school.workoutplanning.helper.AccountHelper;
import com.thumbtack.school.workoutplanning.helper.ErrorHelper;
import com.thumbtack.school.workoutplanning.service.DebugService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource("classpath:test.properties")
@AutoConfigureMockMvc
class AuthControllerTest {
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
    void login_client() throws Exception {
        AccountHelper.registrationClient(mvc, mapper);
        AuthDtoRequest request = AccountHelper.getAuthClientDtoRequest();
        AuthDtoResponse response = AccountHelper.getClientAuthDtoResponse();

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void login_badPassword() throws Exception {
        AccountHelper.registrationClient(mvc, mapper);
        AuthDtoRequest request = AccountHelper.getAuthClientDtoRequest();
        request.setPassword("123456789");
        GlobalErrorHandler.MyError response = ErrorHelper.getBadRequestDtoResponse(BadRequestErrorCode.INVALID_USERNAME_OR_PASSWORD);

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void login_admin() throws Exception {
        AccountHelper.registrationAdmin(mvc, mapper);
        AuthDtoRequest request = AccountHelper.getAuthAdminDtoRequest();
        AuthDtoResponse response = AccountHelper.getAdminAuthDtoResponse();

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void logout() throws Exception {
        AccountHelper.registrationClient(mvc, mapper);
        Cookie cookie = AccountHelper.loginClient(mvc, mapper);

        Cookie expectedCookie = new Cookie(JWT_TOKEN_NAME, null);

        mvc.perform(get("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(cookie().value(expectedCookie.getName(), expectedCookie.getValue()));
    }

    @Test
    void getRole_admin() throws Exception {
        AccountHelper.registrationAdmin(mvc, mapper);
        Cookie cookieAdmin = AccountHelper.loginAdmin(mvc, mapper);
        RoleDtoResponse response = AccountHelper.getRoleAdminDtoResponse();

        mvc.perform(get("/api/auth/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(cookieAdmin))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME));
    }

    @Test
    void getRole_trainer() throws Exception {
        AccountHelper.registrationAdmin(mvc, mapper);
        Cookie cookieAdmin = AccountHelper.loginAdmin(mvc, mapper);
        AccountHelper.registrationTrainer(mvc, mapper, cookieAdmin);
        Cookie cookieTrainer = AccountHelper.loginTrainer(mvc, mapper);
        RoleDtoResponse response = AccountHelper.getRoleTrainerDtoResponse();

        mvc.perform(get("/api/auth/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(cookieTrainer))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME));
    }

    @Test
    void getRole_client() throws Exception {
        AccountHelper.registrationClient(mvc, mapper);
        Cookie cookieClient = AccountHelper.loginClient(mvc, mapper);
        RoleDtoResponse response = AccountHelper.getRoleClientDtoResponse();

        mvc.perform(get("/api/auth/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(cookieClient))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME));
    }
}
package com.thumbtack.school.workoutplanning.controller.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thumbtack.school.workoutplanning.controller.GlobalErrorHandler;
import com.thumbtack.school.workoutplanning.dto.request.account.UpdateAccountDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.account.AuthDtoResponse;
import com.thumbtack.school.workoutplanning.dto.response.account.UserDtoResponse;
import com.thumbtack.school.workoutplanning.exception.BadRequestErrorCode;
import com.thumbtack.school.workoutplanning.exception.InternalErrorCode;
import com.thumbtack.school.workoutplanning.helper.AccountHelper;
import com.thumbtack.school.workoutplanning.helper.ErrorHelper;
import com.thumbtack.school.workoutplanning.model.AuthType;
import com.thumbtack.school.workoutplanning.service.DebugService;
import java.util.ArrayList;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource("classpath:test.properties")
@AutoConfigureMockMvc
class AccountControllerTest {
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
    void getAllUsersByRole_client() throws Exception {
        AccountHelper.registrationClient(mvc, mapper);
        AccountHelper.registrationClientTwo(mvc, mapper);
        AccountHelper.registrationAdmin(mvc, mapper);

        Cookie adminCookie = AccountHelper.loginAdmin(mvc, mapper);
        List<UserDtoResponse> response = new ArrayList<>();
        response.add(AccountHelper.getClientUserDtoResponse());
        response.add(AccountHelper.getClientTwoUserDtoResponse());

        mvc.perform(get("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("role", AuthType.CLIENT.name())
                        .cookie(adminCookie))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void getAllUsersByRole_badRole() throws Exception {
        AccountHelper.registrationClient(mvc, mapper);
        AccountHelper.registrationClientTwo(mvc, mapper);
        AccountHelper.registrationAdmin(mvc, mapper);

        Cookie adminCookie = AccountHelper.loginAdmin(mvc, mapper);
        GlobalErrorHandler.MyError response = ErrorHelper.getBadRequestDtoResponse(BadRequestErrorCode.ROLE_NOT_FOUND);

        mvc.perform(get("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("role", "badRole")
                        .cookie(adminCookie))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void getUserInfo() throws Exception {
        AccountHelper.registrationClient(mvc, mapper);
        Cookie cookie = AccountHelper.loginClient(mvc, mapper);

        UserDtoResponse response = AccountHelper.getClientUserDtoResponse();

        mvc.perform(get("/api/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void getUserInfo_byAdmin() throws Exception {
        AccountHelper.registrationClient(mvc, mapper);
        AccountHelper.registrationAdmin(mvc, mapper);
        Cookie cookie = AccountHelper.loginAdmin(mvc, mapper);

        UserDtoResponse response = AccountHelper.getClientUserDtoResponse();

        mvc.perform(get("/api/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void getUserInfo_forbidden() throws Exception {
        AccountHelper.registrationClient(mvc, mapper);
        AccountHelper.registrationClientTwo(mvc, mapper);
        Cookie cookie = AccountHelper.loginClient(mvc, mapper);

        GlobalErrorHandler.MyError response = ErrorHelper.getBadRequestDtoResponse(InternalErrorCode.FORBIDDEN);

        mvc.perform(get("/api/accounts/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(cookie))
                .andExpect(status().isForbidden())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void getUserInfo_notFound() throws Exception {
        AccountHelper.registrationClient(mvc, mapper);
        Cookie cookie = AccountHelper.loginClient(mvc, mapper);

        GlobalErrorHandler.MyError response = ErrorHelper.getBadRequestDtoResponse(BadRequestErrorCode.USER_NOT_FOUND);

        mvc.perform(get("/api/accounts/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(cookie))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void updateTrainerByAdmin() throws Exception {
        AccountHelper.registrationAdmin(mvc, mapper);
        Cookie cookie = AccountHelper.loginAdmin(mvc, mapper);
        AccountHelper.registrationTrainer(mvc, mapper, cookie);

        UpdateAccountDtoRequest request = AccountHelper.getTrainerUpdateDtoRequest();
        AuthDtoResponse response = AccountHelper.getAuthAfterUpdateDtoResponse();

        mvc.perform(put("/api/accounts/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void updateTrainerByTrainer() throws Exception {
        AccountHelper.registrationAdmin(mvc, mapper);
        Cookie cookie = AccountHelper.loginAdmin(mvc, mapper);
        AccountHelper.registrationTrainer(mvc, mapper, cookie);
        cookie = AccountHelper.loginTrainer(mvc, mapper);

        UpdateAccountDtoRequest request = AccountHelper.getTrainerUpdateDtoRequest();
        AuthDtoResponse response = AccountHelper.getAuthAfterUpdateDtoResponse();

        mvc.perform(put("/api/accounts/trainer")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }


    @Test
    void updateClient() throws Exception {
        AccountHelper.registrationClient(mvc, mapper);
        Cookie cookie = AccountHelper.loginClient(mvc, mapper);

        UpdateAccountDtoRequest request = AccountHelper.getClientUpdateDtoRequest();
        AuthDtoResponse response = AccountHelper.getClientAfterUpdate();

        mvc.perform(put("/api/accounts/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void updateClient_notFound() throws Exception {
        AccountHelper.registrationClient(mvc, mapper);
        Cookie cookie = AccountHelper.loginClient(mvc, mapper);

        UpdateAccountDtoRequest request = AccountHelper.getClientUpdateDtoRequest();
        GlobalErrorHandler.MyError response = ErrorHelper.getBadRequestDtoResponse(BadRequestErrorCode.INVALID_AUTHENTICATION);
        response.getErrors().get(0).setErrorCode(BadRequestErrorCode.INVALID_AUTHENTICATION.getErrorString());
        response.getErrors().get(0).setMessage("User with username or email: trainer not found");

        mvc.perform(put("/api/accounts/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .cookie(cookie))
                .andExpect(status().isForbidden())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }


    @Test
    void updateClient_badUsername() throws Exception {
        AccountHelper.registrationClient(mvc, mapper);
        AccountHelper.registrationClientTwo(mvc, mapper);
        Cookie cookie = AccountHelper.loginClient(mvc, mapper);

        UpdateAccountDtoRequest request = AccountHelper.getClientUpdateDtoRequest();
        GlobalErrorHandler.MyError response = ErrorHelper.getBadRequestDtoResponse(InternalErrorCode.FORBIDDEN);

        mvc.perform(put("/api/accounts/client2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .cookie(cookie))
                .andExpect(status().isForbidden())
                .andExpect(cookie().doesNotExist(JWT_TOKEN_NAME))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void deactivate() throws Exception {
        AccountHelper.registrationAdmin(mvc, mapper);
        Cookie cookie = AccountHelper.loginAdmin(mvc, mapper);
        AccountHelper.registrationTrainer(mvc, mapper, cookie);

        AuthDtoResponse response = AccountHelper.getTrainerAuthDtoResponse();

        mvc.perform(put("/api/accounts/trainer/deactivate")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void deactivate_badUsername() throws Exception {
        AccountHelper.registrationAdmin(mvc, mapper);
        Cookie cookie = AccountHelper.loginAdmin(mvc, mapper);
        AccountHelper.registrationTrainer(mvc, mapper, cookie);

        GlobalErrorHandler.MyError response = ErrorHelper.getBadRequestDtoResponse(BadRequestErrorCode.USER_NOT_FOUND);

        mvc.perform(put("/api/accounts/client/deactivate")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .cookie(cookie))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void activate() throws Exception {
        AccountHelper.registrationAdmin(mvc, mapper);
        Cookie cookie = AccountHelper.loginAdmin(mvc, mapper);
        AccountHelper.registrationTrainer(mvc, mapper, cookie);

        AuthDtoResponse response = AccountHelper.getTrainerAuthDtoResponse();

        mvc.perform(put("/api/accounts/trainer/activate")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }
}
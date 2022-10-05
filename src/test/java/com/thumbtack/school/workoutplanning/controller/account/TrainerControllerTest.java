package com.thumbtack.school.workoutplanning.controller.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thumbtack.school.workoutplanning.dto.request.RegistrationDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.auth.AuthDtoResponse;
import com.thumbtack.school.workoutplanning.helper.AccountHelper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource("classpath:test.properties")
@AutoConfigureMockMvc
class TrainerControllerTest {
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
        AccountHelper.registrationAdmin(mvc, mapper);
        Cookie cookie = AccountHelper.loginAdmin(mvc, mapper);
        RegistrationDtoRequest request = AccountHelper.getTrainerRegistrationDroRequest();
        AuthDtoResponse response = AccountHelper.getTrainerAuthDtoResponse();

        mvc.perform(post("/api/trainer-accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }


}
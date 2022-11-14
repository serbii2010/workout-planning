package com.thumbtack.school.workoutplanning.controller.account;

import com.thumbtack.school.workoutplanning.dto.request.account.AuthDtoRequest;
import com.thumbtack.school.workoutplanning.dto.request.account.CodeDtoRequest;
import com.thumbtack.school.workoutplanning.dto.request.account.RestoreDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.account.AuthDtoResponse;
import com.thumbtack.school.workoutplanning.dto.response.account.RoleDtoResponse;
import com.thumbtack.school.workoutplanning.exception.BadRequestException;
import com.thumbtack.school.workoutplanning.service.UserService;
import com.thumbtack.school.workoutplanning.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthDtoResponse login(@Valid @RequestBody AuthDtoRequest requestDto, HttpServletResponse response)
            throws BadRequestException {
        return userService.auth(requestDto.getUsername(), requestDto.getPassword(), response);
    }

    @PreAuthorize(value = "isAuthenticated()")
    @GetMapping(path = "/logout", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void logout(HttpServletResponse response) {
        userService.logout(response);
    }

    @PreAuthorize(value = "isAuthenticated()")
    @GetMapping(path = "/role", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public RoleDtoResponse getRole() {
        return new RoleDtoResponse(AuthUtils.getRole().name());
    }

    @PostMapping(path = "/reset-password", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void resetPassword(@Valid @RequestBody RestoreDtoRequest requestDto)
            throws BadRequestException {
        userService.resetPassword(requestDto.getEmail());
    }

    @PostMapping(path = "/code", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void code(@Valid @RequestBody CodeDtoRequest requestDto)
            throws BadRequestException {
        userService.acceptCode(requestDto.getCode(), requestDto.getPassword());
    }
}

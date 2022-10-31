package com.thumbtack.school.workoutplanning.controller.account;

import com.thumbtack.school.workoutplanning.dto.request.account.AuthDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.account.AuthDtoResponse;
import com.thumbtack.school.workoutplanning.exception.BadRequestException;
import com.thumbtack.school.workoutplanning.service.UserService;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

@CrossOrigin(origins = "http://localhost:8000/api/auth", maxAge = 3600)
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PreAuthorize("permitAll()")
    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthDtoResponse login(@RequestBody AuthDtoRequest requestDto, HttpServletResponse response)
            throws BadRequestException {
        return userService.auth(requestDto.getUsername(), requestDto.getPassword(), response);
    }

    @PreAuthorize(value = "isAuthenticated()")
    @GetMapping(path = "/logout", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void logout(HttpServletResponse response) {
        userService.logout(response);
    }

}

package com.thumbtack.school.workoutplanning.controller.schedule;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.thumbtack.school.workoutplanning.security.jwt.JwtTokenProvider.JWT_TOKEN_NAME;

@RestController
@PreAuthorize("hasRole(T(com.thumbtack.school.workoutplanning.model.AuthType).CLIENT.name())")
@RequestMapping("/api/schedule")
public class ScheduleController {
    @GetMapping
    public String test(@CookieValue(JWT_TOKEN_NAME) String token) {

        return "Test OK";
    }
}

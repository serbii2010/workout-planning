package com.thumbtack.school.workoutplanning.controller.account;

import com.thumbtack.school.workoutplanning.dto.request.RegistrationDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.auth.AuthDtoResponse;
import com.thumbtack.school.workoutplanning.exception.BadRequestException;
import com.thumbtack.school.workoutplanning.mappers.dto.UserMapper;
import com.thumbtack.school.workoutplanning.model.AuthType;
import com.thumbtack.school.workoutplanning.model.User;
import com.thumbtack.school.workoutplanning.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @PostMapping(path = "/registration", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthDtoResponse addUser(@Valid @RequestBody RegistrationDtoRequest request) throws BadRequestException {
        User user = UserMapper.INSTANCE.registrationDtoToUser(request);
        userService.register(user, AuthType.ADMIN);
        return UserMapper.INSTANCE.userToDtoResponse(user);
    }
}

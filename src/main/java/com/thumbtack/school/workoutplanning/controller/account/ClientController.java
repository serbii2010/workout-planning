package com.thumbtack.school.workoutplanning.controller.account;

import com.thumbtack.school.workoutplanning.dto.request.account.RegistrationDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.account.AuthDtoResponse;
import com.thumbtack.school.workoutplanning.exception.BadRequestException;
import com.thumbtack.school.workoutplanning.mappers.dto.UserMapper;
import com.thumbtack.school.workoutplanning.model.AuthType;
import com.thumbtack.school.workoutplanning.model.User;
import com.thumbtack.school.workoutplanning.service.UserService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/api/client-accounts")
public class ClientController {
    @Autowired
    private UserService userService;

    @PreAuthorize("isAnonymous()")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthDtoResponse registration(@Valid @RequestBody RegistrationDtoRequest request) throws BadRequestException {
        User user = UserMapper.INSTANCE.registrationDtoToUser(request);
        userService.register(user, AuthType.CLIENT);
        return UserMapper.INSTANCE.userToDtoResponse(user);
    }
}

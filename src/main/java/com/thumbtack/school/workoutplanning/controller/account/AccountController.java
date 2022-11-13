package com.thumbtack.school.workoutplanning.controller.account;

import com.thumbtack.school.workoutplanning.dto.request.account.UpdateAccountDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.account.AuthDtoResponse;
import com.thumbtack.school.workoutplanning.dto.response.account.UserDtoResponse;
import com.thumbtack.school.workoutplanning.exception.BadRequestException;
import com.thumbtack.school.workoutplanning.exception.InternalException;
import com.thumbtack.school.workoutplanning.mappers.dto.UserMapper;
import com.thumbtack.school.workoutplanning.model.User;
import com.thumbtack.school.workoutplanning.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    private UserService userService;

    @PreAuthorize(value = "hasRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name())")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDtoResponse> getAllUsersByRole(@RequestParam(value = "role") String role) throws BadRequestException {
        List<User> users = userService.getAllByRole(role);
        return users.stream().map(UserMapper.INSTANCE::userToDtoResponse).collect(Collectors.toList());
    }

    @PreAuthorize(value = "isAuthenticated()")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserDtoResponse getUserInfo(@PathVariable String id) throws BadRequestException, InternalException {
        User user = userService.getById(Long.valueOf(id));
        return UserMapper.INSTANCE.userToDtoResponse(user);
    }

    @PreAuthorize(value = "isAuthenticated()")
    @PutMapping(path = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthDtoResponse update(@PathVariable String username, @Valid @RequestBody UpdateAccountDtoRequest request) throws AccessDeniedException {
        User user = userService.update(username, request);
        return UserMapper.INSTANCE.userToAuthDtoResponse(user);
    }

    @PreAuthorize(value = "hasRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name())")
    @PutMapping(path = "/{username}/deactivate", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthDtoResponse deactivate(@PathVariable String username) throws BadRequestException {
        User user = userService.setActive(username, false);
        return UserMapper.INSTANCE.userToAuthDtoResponse(user);
    }

    @PreAuthorize(value = "hasRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name())")
    @PutMapping(path = "/{username}/activate", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthDtoResponse activate(@PathVariable String username) throws BadRequestException {
        User user = userService.setActive(username, true);
        return UserMapper.INSTANCE.userToAuthDtoResponse(user);
    }
}

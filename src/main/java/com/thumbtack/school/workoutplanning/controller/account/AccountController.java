package com.thumbtack.school.workoutplanning.controller.account;

import com.thumbtack.school.workoutplanning.dto.request.UpdateAccountDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.auth.AuthDtoResponse;
import com.thumbtack.school.workoutplanning.dto.response.auth.UserDetailsResponse;
import com.thumbtack.school.workoutplanning.exception.BadRequestException;
import com.thumbtack.school.workoutplanning.mappers.dto.UserMapper;
import com.thumbtack.school.workoutplanning.model.User;
import com.thumbtack.school.workoutplanning.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    private UserService userService;

    @PreAuthorize(value = "hasRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name())")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDetailsResponse> getAllUsersByRole(@RequestParam(value = "role") String role) throws BadRequestException {
        List<User> users = userService.getAllByRole(role);
        return users.stream().map(UserMapper.INSTANCE::userToDetailsDtoResponse).collect(Collectors.toList());
    }

    @PreAuthorize(value = "hasRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name())")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserDetailsResponse getUserInfo(@PathVariable String id) throws BadRequestException {
        User user = userService.getById(Long.valueOf(id));
        return UserMapper.INSTANCE.userToDetailsDtoResponse(user);
    }

    @PreAuthorize(value = "isAuthenticated()")
    @PutMapping(path = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthDtoResponse update(@PathVariable String username, @Valid @RequestBody UpdateAccountDtoRequest request) throws AccessDeniedException {
        User user = userService.update(username, request);
        return UserMapper.INSTANCE.userToDtoResponse(user);
    }

    @PreAuthorize(value = "hasRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name())")
    @PutMapping(path = "/{username}/deactivate", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthDtoResponse deactivate(@PathVariable String username) throws BadRequestException {
        User user = userService.setActive(username, false);
        return UserMapper.INSTANCE.userToDtoResponse(user);
    }

    @PreAuthorize(value = "hasRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name())")
    @PutMapping(path = "/{username}/activate", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthDtoResponse activate(@PathVariable String username) throws BadRequestException {
        User user = userService.setActive(username, true);
        return UserMapper.INSTANCE.userToDtoResponse(user);
    }
}

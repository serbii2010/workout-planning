package com.thumbtack.school.workoutplanning.controller.subscription;

import com.thumbtack.school.workoutplanning.dto.request.subscription.BaseSubscriptionDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.subscription.BaseSubscriptionDtoResponse;
import com.thumbtack.school.workoutplanning.dto.request.subscription.SubscriptionActivateDtoRequest;
import com.thumbtack.school.workoutplanning.mappers.dto.SubscriptionMapper;
import com.thumbtack.school.workoutplanning.model.SubscriptionType;
import com.thumbtack.school.workoutplanning.service.SubscriptionService;
import com.thumbtack.school.workoutplanning.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping(path = "/api/subscriptions")
public class SubscriptionController {
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private UserService userService;

    @PreAuthorize(value = "isAuthenticated()")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<BaseSubscriptionDtoResponse> filter(
            @RequestParam(name = "type") SubscriptionType type,
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "dateStart", required = false) @Valid
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateStart,
            @RequestParam(name = "dateEnd", required = false) @Valid
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEnd
    ) {
        return subscriptionService.filter(type, username, dateStart, dateEnd)
                .stream()
                .map(SubscriptionMapper.INSTANCE::subscriptionToDtoResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize(value = "hasRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name())")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseSubscriptionDtoResponse insert(@Valid @RequestBody BaseSubscriptionDtoRequest request) {
        return SubscriptionMapper.INSTANCE.subscriptionToDtoResponse(
                subscriptionService.insert(SubscriptionMapper.INSTANCE.requestToSubscription(request, userService))
        );
    }

    @PreAuthorize(value = "hasRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name())")
    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseSubscriptionDtoResponse activate(@PathVariable Long id,
                                                @Valid @RequestBody SubscriptionActivateDtoRequest request) {
        return SubscriptionMapper.INSTANCE.subscriptionToDtoResponse(
                subscriptionService.setActive(id, request.getIsActive())
        );
    }
}

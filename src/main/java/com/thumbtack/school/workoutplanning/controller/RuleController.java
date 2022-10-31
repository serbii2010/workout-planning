package com.thumbtack.school.workoutplanning.controller;

import com.thumbtack.school.workoutplanning.dto.request.options.OptionDtoRequest;
import com.thumbtack.school.workoutplanning.dto.response.option.OptionDtoResponse;
import com.thumbtack.school.workoutplanning.exception.BadRequestException;
import com.thumbtack.school.workoutplanning.mappers.dto.OptionMapper;
import com.thumbtack.school.workoutplanning.service.RuleService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping(path = "/api/rules")
public class RuleController {
    @Autowired
    private RuleService ruleService;

    @PreAuthorize(value = "hasRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name())")
    @GetMapping(path = "/{rule}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public OptionDtoResponse getRule(@PathVariable String rule) throws BadRequestException {
        return OptionMapper.INSTANCE.optionToDtoResponse(ruleService.getRule(rule));
    }

    @PreAuthorize(value = "hasRole(T(com.thumbtack.school.workoutplanning.model.AuthType).ADMIN.name())")
    @PostMapping(path = "/{rule}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public OptionDtoResponse setRule(@PathVariable String rule, @Valid @RequestBody OptionDtoRequest request) throws BadRequestException {
        return OptionMapper.INSTANCE.optionToDtoResponse(ruleService.setRule(rule, request.getValue()));
    }
}

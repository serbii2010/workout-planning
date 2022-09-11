package com.thumbtack.school.workoutplanning.controller.account;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AccountController {
    @GetMapping
    public String test() {
        return "Hello world controller";
    }
}

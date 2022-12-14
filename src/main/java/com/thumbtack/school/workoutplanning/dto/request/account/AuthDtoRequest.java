package com.thumbtack.school.workoutplanning.dto.request.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDtoRequest {
    private String username;
    private String password;
}

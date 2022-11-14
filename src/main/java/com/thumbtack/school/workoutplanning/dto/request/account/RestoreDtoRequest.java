package com.thumbtack.school.workoutplanning.dto.request.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestoreDtoRequest {
    private String email;
}

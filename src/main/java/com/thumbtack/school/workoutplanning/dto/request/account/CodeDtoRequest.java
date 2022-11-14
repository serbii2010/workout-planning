package com.thumbtack.school.workoutplanning.dto.request.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeDtoRequest {
    @NotNull
    @Min(1000)
    private Integer code;
    @NotBlank
    private String password;
}

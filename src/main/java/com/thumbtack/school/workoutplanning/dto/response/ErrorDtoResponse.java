package com.thumbtack.school.workoutplanning.dto.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDtoResponse {
    private String errorCode;
    private String field;
    private String message;
}

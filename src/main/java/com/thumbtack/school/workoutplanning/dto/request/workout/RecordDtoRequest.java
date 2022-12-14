package com.thumbtack.school.workoutplanning.dto.request.workout;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordDtoRequest {
    @Min(1)
    private Long workoutId;
    @NotBlank
    private String username;
}

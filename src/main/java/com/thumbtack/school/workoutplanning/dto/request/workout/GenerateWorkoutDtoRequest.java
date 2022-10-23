package com.thumbtack.school.workoutplanning.dto.request.workout;

import com.thumbtack.school.workoutplanning.validator.DateFormat;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateWorkoutDtoRequest {
    @NotBlank
    @DateFormat
    private String dateStart;
    @NotBlank
    @DateFormat
    private String dateEnd;
    @NotBlank
    private String period;
    @NotBlank
    private String trainer;
    @NotBlank
    private String typeWorkout;
    @Min(1)
    private Integer duration;
    @Min(1)
    private Integer totalSeats;
    @NotBlank
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]", message = "format field 'HH:MM'")
    private String timeStart;
}

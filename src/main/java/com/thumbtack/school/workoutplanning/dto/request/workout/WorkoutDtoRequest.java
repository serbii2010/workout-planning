package com.thumbtack.school.workoutplanning.dto.request.workout;

import java.time.LocalDate;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutDtoRequest {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private String timeStart;
    @NotBlank
    private String trainer;
    @Min(1)
    private Integer duration;
    @NotBlank
    private String typeWorkout;
    @Min(1)
    private Integer totalSeats;
    @NotNull
    private Integer availableSeats;
}

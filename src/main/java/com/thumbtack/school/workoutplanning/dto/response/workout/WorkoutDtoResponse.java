package com.thumbtack.school.workoutplanning.dto.response.workout;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutDtoResponse {
    private Integer id;
    private LocalDate date;
    private String timeStart;
    private String trainer;
    private Integer duration;
    private String typeWorkout;
    private Integer totalSeats;
    private Integer availableSeats;
}

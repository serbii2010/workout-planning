package com.thumbtack.school.workoutplanning.dto.response.workout;

import com.thumbtack.school.workoutplanning.model.RecordStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private String recordStatus;

    public WorkoutDtoResponse(Integer id,
                              LocalDate date,
                              String timeStart,
                              String trainer,
                              Integer duration,
                              String typeWorkout,
                              Integer totalSeats,
                              Integer availableSeats) {
        this(id, date, timeStart, trainer, duration, typeWorkout, totalSeats, availableSeats, RecordStatus.UNDEFINED.name());
    }
}

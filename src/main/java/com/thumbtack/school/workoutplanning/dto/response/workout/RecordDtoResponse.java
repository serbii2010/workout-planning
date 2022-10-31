package com.thumbtack.school.workoutplanning.dto.response.workout;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordDtoResponse {
    private Integer id;
    private String username;
    private String firstName;
    private String lastName;
    private String trainerUsername;
    private String dateTimeWorkout;
    private String dateTimeRecord;
    private String status;
}

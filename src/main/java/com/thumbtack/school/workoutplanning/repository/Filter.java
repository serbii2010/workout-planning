package com.thumbtack.school.workoutplanning.repository;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Filter {
    private String key;
    private Operator operation;
    private Object value;
}

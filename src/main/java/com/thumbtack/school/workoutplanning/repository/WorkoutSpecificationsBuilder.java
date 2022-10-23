package com.thumbtack.school.workoutplanning.repository;

import com.thumbtack.school.workoutplanning.model.Workout;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;

import static org.springframework.data.jpa.domain.Specification.where;

public class WorkoutSpecificationsBuilder {
    private final List<Filter> params;

    public WorkoutSpecificationsBuilder() {
        params = new ArrayList<>();
    }

    public void with(String key, Operator operation, Object value) {
        params.add(new Filter(key, operation, value));
    }

    public Specification<Workout> build() {
        if (params.size() == 0) {
            return null;
        }

        List<Specification<Workout>> specs = params.stream()
                .map(WorkoutSpecification::new)
                .collect(Collectors.toList());

        Specification<Workout> result = where(specs.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = result.and(specs.get(i));
        }
        return result;
    }
}

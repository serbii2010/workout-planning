package com.thumbtack.school.workoutplanning.repository;

import com.thumbtack.school.workoutplanning.model.Subscription;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;

public class SubscriptionSpecificationBuilder {
    private final List<Filter> params;

    public SubscriptionSpecificationBuilder() {
        params = new ArrayList<>();
    }

    public void with(String key, Operator operation, Object value) {
        params.add(new Filter(key, operation, value));
    }

    public Specification<Subscription> build() {
        if (params.size() == 0) {
            return null;
        }

        List<Specification<Subscription>> specs = params.stream()
                .map(SubscriptionSpecification::new)
                .collect(Collectors.toList());

        Specification<Subscription> result = where(specs.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = result.and(specs.get(i));
        }
        return result;
    }
}

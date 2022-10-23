package com.thumbtack.school.workoutplanning.repository;

import com.thumbtack.school.workoutplanning.model.Workout;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class WorkoutSpecification implements Specification<Workout> {
    private final Filter filter;

    WorkoutSpecification(Filter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<Workout> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        if (filter.getOperation().equals(Operator.LARGE)) {
            return builder.greaterThanOrEqualTo(root.get(filter.getKey()).as(String.class), filter.getValue().toString());
        } else if (filter.getOperation().equals(Operator.LESS)) {
            return builder.lessThanOrEqualTo(root.get(filter.getKey()).as(String.class), filter.getValue().toString());
        } else if (filter.getOperation().equals(Operator.LIKES)) {
            return builder.like(root.get(filter.getKey()), "%" + filter.getValue() + "%");
        } else if (filter.getOperation().equals(Operator.EQUALS)) {
            return builder.equal(root.get(filter.getKey()), filter.getValue());
        }
        return null;
    }
}

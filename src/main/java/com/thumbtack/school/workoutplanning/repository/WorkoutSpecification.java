package com.thumbtack.school.workoutplanning.repository;

import com.thumbtack.school.workoutplanning.model.Record;
import com.thumbtack.school.workoutplanning.model.Workout;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
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
        switch (filter.getOperation()) {
            case LARGE:
                return builder.greaterThanOrEqualTo(root.get(filter.getKey()).as(String.class), filter.getValue().toString());
            case LESS:
                return builder.lessThanOrEqualTo(root.get(filter.getKey()).as(String.class), filter.getValue().toString());
            case LIKES:
                return builder.like(root.get(filter.getKey()), "%" + filter.getValue() + "%");
            case EQUALS:
                return builder.equal(root.get(filter.getKey()), filter.getValue());
            case EQUALS_STATUS: {
                Join<Workout, Record> workoutRecordJoin = root.join("records");
                return builder.equal(workoutRecordJoin.get("status"), filter.getValue());
            }
            case EQUALS_USER: {
                Join<Workout, Record> workoutRecordJoin = root.join("records");
                return builder.equal(workoutRecordJoin.get("user"), filter.getValue());
            }
            default:
                return null;
        }
    }
}

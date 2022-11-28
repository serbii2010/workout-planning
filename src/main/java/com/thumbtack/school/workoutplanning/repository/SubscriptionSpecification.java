package com.thumbtack.school.workoutplanning.repository;

import com.thumbtack.school.workoutplanning.model.Subscription;
import com.thumbtack.school.workoutplanning.model.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class SubscriptionSpecification implements Specification<Subscription> {
    private final Filter filter;

    SubscriptionSpecification(Filter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<Subscription> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Join<Subscription, User> subscriptionUserJoin = root.join("user");
        switch (filter.getOperation()) {
            case LARGE:
                return builder.greaterThanOrEqualTo(root.get(filter.getKey()).as(String.class), filter.getValue().toString());
            case LESS:
                return builder.lessThanOrEqualTo(root.get(filter.getKey()).as(String.class), filter.getValue().toString());
            case LIKES:
                return builder.like(root.get(filter.getKey()), "%" + filter.getValue() + "%");
            case EQUALS:
                return builder.equal(root.get(filter.getKey()), filter.getValue());
            case LIKES_USER:
                return builder.like(subscriptionUserJoin.get("username"), "%" + filter.getValue() + "%");
            case EQUALS_USER:
                return builder.equal(subscriptionUserJoin.get("username"), filter.getValue());
            default:
                return null;
        }
    }
}

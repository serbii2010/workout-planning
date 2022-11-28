package com.thumbtack.school.workoutplanning.model;


import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "subscription_limited",
        schema = "public"
)
@RequiredArgsConstructor
public class SubscriptionLimited extends Subscription {
    private Integer total;
    private Integer remaining;

    @Override
    public SubscriptionType getType() {
        return SubscriptionType.LIMITED;
    }
}

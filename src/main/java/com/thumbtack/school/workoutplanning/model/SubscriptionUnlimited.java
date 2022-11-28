package com.thumbtack.school.workoutplanning.model;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "subscription_unlimited",
        schema = "public"
)
@RequiredArgsConstructor
public class SubscriptionUnlimited extends Subscription {
    private LocalDate fromDate;
    private LocalDate toDate;

    @Override
    public SubscriptionType getType() {
        return SubscriptionType.UNLIMITED;
    }
}

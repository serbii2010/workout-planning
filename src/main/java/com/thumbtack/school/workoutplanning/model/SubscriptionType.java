package com.thumbtack.school.workoutplanning.model;

import lombok.Getter;

public enum SubscriptionType {
    LIMITED(SubscriptionType.LIMITED_VALUE),
    UNLIMITED(SubscriptionType.UNLIMITED_VALUE);

    @Getter
    public static final String LIMITED_VALUE = "LIMITED";
    @Getter
    public static final String UNLIMITED_VALUE = "UNLIMITED";

    @Getter
    private final String value;
    SubscriptionType(String value) {
        this.value = value;
    }
}

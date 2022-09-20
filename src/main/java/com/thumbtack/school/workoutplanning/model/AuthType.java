package com.thumbtack.school.workoutplanning.model;

public enum AuthType {
    ADMIN("ROLE_ADMIN"),
    TRAINER("ROLE_TRAINER"),
    CLIENT("ROLE_CLIENT");

    private final String type;

    AuthType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

package com.thumbtack.school.workoutplanning.exception;

public enum BadRequestErrorCode {
    USER_NOT_FOUND("User not registered"),
    USERNAME_ALREADY_USED("Username or email already used"),
    BAD_FIELD_COOKIE("Cookie not found"),
    INVALID_USERNAME_OR_PASSWORD("Invalid username or password"),
    ROLE_NOT_FOUND("Role not found"),
    INVALID_AUTHENTICATION("Invalid authentication token");

    private String error;

    BadRequestErrorCode(String error) {
        this.error = error;
    }

    public String getErrorString() {
        return this.error;
    }

    public void setErrorString(String errorString) {
        this.error = errorString;
    }
}

package com.thumbtack.school.workoutplanning.exception;

public enum BadRequestErrorCode {
    USER_NOT_FOUND("User not registered"),
    USERNAME_ALREADY_USED("Username or email already used"),
    BAD_FIELD_COOKIE("Cookie not found"),
    INVALID_USERNAME_OR_PASSWORD("Invalid username or password"),
    ROLE_NOT_FOUND("Role not found"),
    INVALID_AUTHENTICATION("Invalid authentication token"),
    DATETIME_WORKOUT_ALREADY_EXISTS("Datetime workout already exist"),
    WORKOUT_NOT_FOUND("Workout not found"),
    BAD_ID_REQUEST("Bad id in request"),
    TRAINER_NOT_FOUND("Trainer not found"),
    BAD_DATE_OR_TIME("Bad Date or Time in request"),
    UNEXPECTED_TYPE("Unexpected type in request");

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

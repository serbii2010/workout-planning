package com.thumbtack.school.workoutplanning.exception;

public enum InternalErrorCode {
    INTERNAL_ERROR("Internal error"),
    UNKNOWN_ROLE("Unknown Role"),
    FORBIDDEN("Action forbidden"),
    NOT_FOUND("Endpoint not found");

    InternalErrorCode(String error) {
        setErrorString(error);
    }

    private String error;

    public String getErrorString() {
        return this.error;
    }

    public void setErrorString(String errorString) {
        this.error = errorString;
    }
}

package com.thumbtack.school.workoutplanning.exception;

public class BadRequestException extends Exception {
    private final BadRequestErrorCode errorCode;

    public BadRequestException(BadRequestErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BadRequestException(BadRequestErrorCode errorCode) {
        this(errorCode, errorCode.getErrorString());
    }

    public BadRequestErrorCode getErrorCode() {
        return this.errorCode;
    }
}

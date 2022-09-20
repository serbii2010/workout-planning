package com.thumbtack.school.workoutplanning.exception;

public class InternalException extends Exception {
    private final InternalErrorCode errorCode;

    public InternalException(InternalErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public InternalException(InternalErrorCode errorCode) {
        this(errorCode, errorCode.getErrorString());
    }

    public InternalErrorCode getErrorCode() {
        return this.errorCode;
    }
}

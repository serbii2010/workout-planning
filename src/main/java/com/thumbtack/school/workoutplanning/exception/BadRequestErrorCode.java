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
    UNEXPECTED_TYPE("Unexpected type in request"),
    RECORD_ALREADY_EXIST("Record already exist"),
    INVALID_USERNAME("Invalid username"),
    NOT_AVAILABLE_SEATS("Workout not exists available seats"),
    RECORD_NOT_FOUND("Record not found"),
    OPTION_NOT_FOUND("Option not found"),
    WORKOUT_ALREADY_STARTED("Workout already started"),
    BAD_RULE_NAME("Bad rule name"),
    BAD_RULE_VALUE("Bad rule value"),
    CLIENT_NOT_FOUND("Client not found"),
    RULE_NOT_SET("Rule not set"),
    BAD_TYPE_PARAM("Bad type param"),
    BAD_REDIRECT_URL("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication"), 
    CODE_EXPIRED("Restore code expired"),
    ACCOUNT_HAS_BAD_STATE("Account has bad state"),
    CODE_NOT_FOUND("Code not found");

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

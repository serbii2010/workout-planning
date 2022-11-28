package com.thumbtack.school.workoutplanning.helper;

import com.thumbtack.school.workoutplanning.controller.GlobalErrorHandler;
import com.thumbtack.school.workoutplanning.dto.response.ErrorDtoResponse;
import com.thumbtack.school.workoutplanning.exception.BadRequestErrorCode;
import com.thumbtack.school.workoutplanning.exception.InternalErrorCode;

public class ErrorHelper {
    public static GlobalErrorHandler.MyError getBadRequestDtoResponse(BadRequestErrorCode code) {
        GlobalErrorHandler.MyError error = new GlobalErrorHandler.MyError();
        error.getErrors().add(new ErrorDtoResponse(
                code.name(),
                null,
                code.getErrorString()
        ));
        return error;
    }

    public static GlobalErrorHandler.MyError getBadRequestDtoResponse(InternalErrorCode code) {
        GlobalErrorHandler.MyError error = new GlobalErrorHandler.MyError();
        error.getErrors().add(new ErrorDtoResponse(
                code.name(),
                null,
                code.getErrorString()
        ));
        return error;
    }
}

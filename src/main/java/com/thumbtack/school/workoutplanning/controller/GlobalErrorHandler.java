package com.thumbtack.school.workoutplanning.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thumbtack.school.workoutplanning.dto.response.ErrorDtoResponse;
import com.thumbtack.school.workoutplanning.exception.BadRequestErrorCode;
import com.thumbtack.school.workoutplanning.exception.BadRequestException;
import com.thumbtack.school.workoutplanning.exception.InternalErrorCode;
import com.thumbtack.school.workoutplanning.exception.InternalException;
import com.thumbtack.school.workoutplanning.security.jwt.JwtAuthenticationException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Aspect
@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public MyError handleMyServerError(BadRequestException exception) {
        final MyError error = new MyError();
        ErrorDtoResponse dtoResponse = new ErrorDtoResponse();
        dtoResponse.setErrorCode(exception.getErrorCode().name());
        dtoResponse.setMessage(exception.getMessage());
        error.getErrors().add(dtoResponse);
        return error;
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public MyError handleJwtAuthException(BadRequestException exception) {
        MyError error = new MyError();
        error.getErrors().add(new ErrorDtoResponse(InternalErrorCode.FORBIDDEN.getErrorString(), null, null));
        return error;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public MyError onConstraintValidationException(ConstraintViolationException e) {
        final MyError error = new MyError();
        e.getConstraintViolations().forEach(violation -> {
            ErrorDtoResponse errorDtoResponse = new ErrorDtoResponse();
            errorDtoResponse.setErrorCode(BadRequestErrorCode.BAD_TYPE_PARAM.getErrorString());
            errorDtoResponse.setField(violation.getPropertyPath().toString());
            errorDtoResponse.setMessage(violation.getMessage());
            error.getErrors().add(errorDtoResponse);
        });
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public MyError handleValidation(MethodArgumentNotValidException exc) {
        final MyError error = new MyError();
        exc.getBindingResult().getFieldErrors().forEach(fieldError -> {
            ErrorDtoResponse errorDtoResponse = new ErrorDtoResponse();
            errorDtoResponse.setErrorCode(fieldError.getCode());
            errorDtoResponse.setField(fieldError.getField());
            errorDtoResponse.setMessage(fieldError.getDefaultMessage());
            error.getErrors().add(errorDtoResponse);
        });
        exc.getBindingResult().getGlobalErrors().forEach(err -> {
            ErrorDtoResponse errorDtoResponse = new ErrorDtoResponse();
            errorDtoResponse.setErrorCode(err.getCode());
            errorDtoResponse.setField("Global");
            errorDtoResponse.setMessage(err.getDefaultMessage());
            error.getErrors().add(errorDtoResponse);
        });
        return error;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public MyError badType(HttpServletRequest request, HttpMessageNotReadableException exception) {
        MyError error = new MyError();
        if (exception.getCause() instanceof JsonParseException) {
            JsonParseException cause = (JsonParseException) exception.getCause();
            error.getErrors().add(new ErrorDtoResponse(
                    BadRequestErrorCode.UNEXPECTED_TYPE.getErrorString(),
                    "",
                    cause.getOriginalMessage()));
            log.error("Error: {} {}", request, exception);
            return error;
        }
        if (exception.getCause() instanceof InvalidFormatException) {
            InvalidFormatException cause = (InvalidFormatException) exception.getCause();
            error.getErrors().add(new ErrorDtoResponse(
                    BadRequestErrorCode.UNEXPECTED_TYPE.getErrorString(),
                    cause.getPath().get(0).getFieldName(),
                    cause.getOriginalMessage()));
            log.error("Error: {} {}", request, exception);
            return error;
        }
        error.getErrors().add(new ErrorDtoResponse(
                BadRequestErrorCode.UNEXPECTED_TYPE.getErrorString(),
                "",
                exception.getCause().getMessage()));
        log.error("Error: {} {}", request, exception);
        return error;
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public MyError usernameNotFound(HttpServletRequest request, Exception e) {
        MyError error = new MyError();
        error.getErrors().add(new ErrorDtoResponse(BadRequestErrorCode.INVALID_AUTHENTICATION.getErrorString(), null, e.getMessage()));
        log.error("Error: {} {}", request, e);
        return error;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public MyError forbidden(AccessDeniedException exception) {
        MyError error = new MyError();
        error.getErrors().add(new ErrorDtoResponse(InternalErrorCode.FORBIDDEN.name(), null, exception.getMessage()));
        return error;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public MyError badInputParam(HttpServletRequest request, MethodArgumentTypeMismatchException e) {
        MyError error = new MyError();
        if (e.getCause() instanceof NumberFormatException) {
            NumberFormatException cause = (NumberFormatException) e.getCause();
            error.getErrors().add(new ErrorDtoResponse(
                    BadRequestErrorCode.BAD_TYPE_PARAM.getErrorString(),
                    e.getName(),
                    cause.getMessage()));
            log.error("Error: {} {}", request, e);
            return error;
        }
        error.getErrors().add(new ErrorDtoResponse(
                BadRequestErrorCode.BAD_TYPE_PARAM.getErrorString(),
                e.getName(),
                e.getCause().getMessage()));
        log.error("Error: {} {}", request, e);
        return error;
    }

    @ExceptionHandler(DateTimeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public MyError badDate(HttpServletRequest request, DateTimeParseException exception) {
        MyError error = new MyError();
        error.getErrors().add(new ErrorDtoResponse(
                BadRequestErrorCode.BAD_DATE_OR_TIME.getErrorString(),
                "",
                exception.getMessage()));
        log.error("Error: {} {}", request, exception);
        return error;
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public MyError handleCookie(MissingRequestCookieException exception) {
        final MyError error = new MyError();
        ErrorDtoResponse dtoResponse = new ErrorDtoResponse();
        dtoResponse.setErrorCode(BadRequestErrorCode.BAD_FIELD_COOKIE.getErrorString());
        dtoResponse.setMessage(exception.getMessage());
        dtoResponse.setField(exception.getCookieName());
        error.getErrors().add(dtoResponse);
        return error;
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public void notAllowed(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        log.warn("Method not allowed {} {}", request, e);
    }

    @ExceptionHandler(InternalException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public MyError internalError(HttpServletRequest request, Exception e) {
        MyError error = getInternalError();
        log.error("Error: {} {}", request, e);
        return error;
    }

    // other errors
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public MyError systemError(HttpServletRequest request, Exception e) {
        MyError error = getInternalError();
        log.error("Error: {} {}", request, e);
        return error;
    }

    public static class MyError {
        private List<ErrorDtoResponse> errors = new ArrayList<>();

        public List<ErrorDtoResponse> getErrors() {
            return errors;
        }

        public void setAllErrors(List<ErrorDtoResponse> Errors) {
            this.errors = Errors;
        }
    }

    private MyError getInternalError() {
        MyError error = new MyError();
        error.getErrors().add(new ErrorDtoResponse(InternalErrorCode.INTERNAL_ERROR.getErrorString(), null, null));
        return error;
    }
}

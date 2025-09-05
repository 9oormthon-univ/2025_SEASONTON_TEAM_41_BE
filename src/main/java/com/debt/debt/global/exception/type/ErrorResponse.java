package com.debt.debt.global.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String code;
    private String message;
    private List<FieldErrorDetail> errors;

    public static ErrorResponse of(ErrorCode errorCode, List<FieldErrorDetail> errors) {
        return new ErrorResponse(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage(), errors);
    }

    public static ErrorResponse of(HttpStatus status, String message, List<FieldErrorDetail> errors) {
        return new ErrorResponse(status.value(), String.valueOf(status.value()), message, errors);
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(status.value(), String.valueOf(status.value()), message, null);
    }
}
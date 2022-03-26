package com.nik.yourcodereview.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ValidationException extends RestServiceException {

    public ValidationException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
package com.nik.yourcodereview.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RestServiceException extends RuntimeException {

    private final HttpStatus httpStatus;

    public RestServiceException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
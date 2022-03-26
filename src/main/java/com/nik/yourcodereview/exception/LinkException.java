package com.nik.yourcodereview.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LinkException extends RestServiceException {

    public LinkException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}

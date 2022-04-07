package com.nik.yourcodereview.exception;

import com.nik.yourcodereview.model.dto.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {LinkException.class, ValidationException.class})
    protected ResponseEntity<Error> handleErrors(RestServiceException restServiceException) {
        return ResponseEntity.status(restServiceException.getHttpStatus()).contentType(MediaType.APPLICATION_JSON)
                .body(new Error().setMessage(restServiceException.getMessage()));
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException e) {
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
                .body(new Error().setMessage(e.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<Error> handleErrors(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
                .body(new Error().setMessage(e.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("Missing required parameters", ex);
        return ResponseEntity.status(status).headers(headers).contentType(MediaType.APPLICATION_JSON)
                .body(new Error().setMessage("Missing required parameters: " + ex.getLocalizedMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("Not valid request parameter", ex);
        return ResponseEntity.status(status).headers(headers).contentType(MediaType.APPLICATION_JSON)
                .body(new Error().setMessage("Not valid request parameter: " + ex.getBindingResult().getFieldErrors()
                        .stream().map(fieldError -> fieldError.getField() + " - " + fieldError.getDefaultMessage())
                        .collect(Collectors.joining(", "))));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("Not readable request body", ex);
        return ResponseEntity.status(status).headers(headers).contentType(MediaType.APPLICATION_JSON)
                .body(new Error().setMessage("Not readable request body: " + ex.getMostSpecificCause().getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.error("Unknown error", ex);
        return ResponseEntity.status(status).headers(headers).contentType(MediaType.APPLICATION_JSON)
                .body(new Error().setMessage("Unknown error: " + ex.getMessage()));
    }
}

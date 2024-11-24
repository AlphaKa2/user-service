package com.alphaka.userservice.exception.handler;

import com.alphaka.userservice.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DeserializationExceptionHandler {

    private static final String UNREADABLE_REQUEST_CODE = "USR-009";
    private static final String UNREADABLE_REQUEST_MESSAGE = "읽을 수 없는 요청입니다.";

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), UNREADABLE_REQUEST_CODE,
                UNREADABLE_REQUEST_MESSAGE);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}

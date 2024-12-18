package com.alphaka.userservice.exception.handler;

import com.alphaka.userservice.dto.response.ErrorResponse;
import com.alphaka.userservice.exception.ErrorCode;
import com.alphaka.userservice.exception.custom.CustomException;
import com.amazonaws.SdkClientException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class S3ClientExceptionHandler {

    @ExceptionHandler(SdkClientException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(SdkClientException exception) {

        ErrorCode errorCode = ErrorCode.GENERATING_PRESIGEND_URL_FAILURE;
        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getStatus(),
                errorCode.getCode(),
                errorCode.getMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode.getStatus()));
    }
}

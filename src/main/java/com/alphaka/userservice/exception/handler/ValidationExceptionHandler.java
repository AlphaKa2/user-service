package com.alphaka.userservice.exception.handler;

import com.alphaka.userservice.dto.response.ErrorResponse;
import com.alphaka.userservice.exception.ErrorCode;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ValidationExceptionHandler {

    // 요청 DTO 검증 실패 시
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        log.error("요청에 대한 검증이 실패하였습니다.");

        // 모든 필드 오류를 수집
        StringBuilder message = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            message.append(fieldName).append(":").append(errorMessage).append("\n");
        });

        ErrorCode errorCode = ErrorCode.VALIDATION_FAILURE;
        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getStatus(),
                errorCode.getCode(),
                message.toString());

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode.getStatus()));

    }
}

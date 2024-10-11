package com.alphaka.userservice.exception.handler;

import com.alphaka.userservice.dto.response.ErrorResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ValidationExceptionHandler {

    private static final String VALIDATION_FAIL_CODE = "USR-009";

    // 요청 DTO 검증 실패 시
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        // 모든 필드 오류를 수집
        StringBuilder message = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            message.append(fieldName).append(":").append(errorMessage).append("\n");
        });

        ErrorResponse response = new ErrorResponse(ex.getStatusCode().value(), VALIDATION_FAIL_CODE,
                message.toString());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }
}

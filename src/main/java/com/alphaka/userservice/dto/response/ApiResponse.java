package com.alphaka.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private static final String SUCCESS_STATUS = "success";
    private static final String FAIL_STATUS = "fail";
    private static final String ERROR_STATUS = "error";

    private int code;
    private String status;
    // 성공 시 응답 객체, 실패 시 예외 객체
    private T data;
    // 보조 메시지
    private String message;

    public static <T> ApiResponse<T> createSuccessResponseWithData(int code, T data) {
        return ApiResponse.<T>builder()
                .code(code)
                .status(SUCCESS_STATUS)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> createSuccessResponse(int code) {
        return ApiResponse.<T>builder()
                .code(code)
                .status(SUCCESS_STATUS)
                .build();
    }

    public static <T> ApiResponse<T> createErrorResponseWithExceptions(int code, T errors) {
        return ApiResponse.<T>builder()
                .code(code)
                .status(FAIL_STATUS)
                .data(errors)
                .build();
    }
}

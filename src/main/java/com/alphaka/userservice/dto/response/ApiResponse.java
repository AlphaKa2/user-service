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

    // Http status
    private int code;
    private T data;
    // 보조 메시지
    private String message;

    public static <T> ApiResponse<T> createSuccessResponseWithData(int code, T data) {
        return ApiResponse.<T>builder()
                .code(code)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> createSuccessResponse(int code) {
        return ApiResponse.<T>builder()
                .code(code)
                .data(null)
                .message(null)
                .build();
    }
}

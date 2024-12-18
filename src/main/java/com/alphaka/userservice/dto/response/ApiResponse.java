package com.alphaka.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ApiResponse<T> {

    // Http status
    private int status;

    @Nullable
    private T data;
    // 보조 메시지
    @Nullable
    private String message;

    public static <T> ApiResponse<T> createSuccessResponseWithData(int status, T data) {
        return ApiResponse.<T>builder()
                .status(status)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> createSuccessResponse(int status) {
        return ApiResponse.<T>builder()
                .status(status)
                .data(null)
                .message(null)
                .build();
    }
}

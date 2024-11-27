package com.alphaka.userservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TripMbtiUpdateRequest {

    @NotBlank
    @Schema(description = "여행 MBTI", example = "ABLJ", requiredMode = Schema.RequiredMode.REQUIRED)
    private String mbti;
}

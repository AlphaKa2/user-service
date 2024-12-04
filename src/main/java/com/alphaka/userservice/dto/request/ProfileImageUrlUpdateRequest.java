package com.alphaka.userservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImageUrlUpdateRequest {

    @NotBlank(message = "프로필 이미지 경로는 필수 입력값입니다.")
    @Schema(description = "s3 presigned url", requiredMode = Schema.RequiredMode.REQUIRED)
    private String profileImageUrl;

}

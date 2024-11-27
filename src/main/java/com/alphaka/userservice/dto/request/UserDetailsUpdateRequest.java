package com.alphaka.userservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsUpdateRequest {

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(min = 3, max = 20, message = "닉네임은 최소 3자에서 최대 20자까지 입력 가능합니다.")
    @Schema(description = "닉네임", example = "gildong12", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickname;

    @Schema(description = "프로필 소개글", example = "안녕하세요~", requiredMode = RequiredMode.NOT_REQUIRED)
    private String profileDescription;

}

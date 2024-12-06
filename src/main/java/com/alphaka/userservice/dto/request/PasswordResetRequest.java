package com.alphaka.userservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequest {

    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Schema(description = "사용자의 이메일", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "sms 인증 토큰은 필수 입력값입니다.")
    @Schema(description = "sms 인증 토큰", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTbXNDb25maXJtYXRpb24iLCJleHAiOjE3MzM0NjI4MjMsInBob25lTnVtYmVyIjoiMDEwMDAwMDAwMDAifQ.8Lur3HULIkGPJeqZOB1cqduqpp8DeFbCoYE3GCiv31qTV_GrOr6oi7NerhaWXkslcNKJIdECJQtWDdlw2vQmgw", requiredMode = Schema.RequiredMode.REQUIRED)
    private String smsConfirmation;

    @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,}$",
            message = "비밀번호는 최소 1개의 숫자, 문자, 특수 문자를 포함해야 합니다.")
    @Schema(description = "새 비밀번호", example = "NewP@ssw0rd!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}

package com.alphaka.userservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateRequest {

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,}$",
            message = "비밀번호는 최소 1개의 숫자, 문자, 특수 문자를 포함해야 합니다.")
    @Schema(description = "이전 비밀번호", example = "P@ssw0rd!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String previousPassword;

    @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,}$",
            message = "비밀번호는 최소 1개의 숫자, 문자, 특수 문자를 포함해야 합니다.")
    @Schema(description = "새 비밀번호", example = "NewP@ssw0rd!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}

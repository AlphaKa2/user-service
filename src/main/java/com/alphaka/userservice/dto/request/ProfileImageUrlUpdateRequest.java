package com.alphaka.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImageUrlUpdateRequest {

    @NotBlank(message = "프로필 이미지 경로는 필수 입력값입니다.")
    private String profileImageUrl;

}

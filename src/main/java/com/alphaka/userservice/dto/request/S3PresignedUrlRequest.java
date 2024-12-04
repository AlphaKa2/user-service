package com.alphaka.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class S3PresignedUrlRequest {

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Pattern(regexp = "^[^\\s]+$", message = "파일 이름에 공백이 포함될 수 없습니다.")
    String fileName;

    @NotBlank(message = "파일 타입은 필수 입력값입니다.")
    @Pattern(regexp = "^image/.*$", message = "이미지 파일만 업로드 가능합니다.")
    private String contentType;
}

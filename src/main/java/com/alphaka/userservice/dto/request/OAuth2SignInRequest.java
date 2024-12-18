package com.alphaka.userservice.dto.request;

import com.alphaka.userservice.entity.Role;
import com.alphaka.userservice.entity.SocialType;
import com.alphaka.userservice.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
public class OAuth2SignInRequest {

    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Schema(description = "사용자의 이메일 주소", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotNull(message = "소셜타입은 필수 입력값입니다.")
    @Schema(description = "소셜 로그인 타입", example = "GOOGLE", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"GOOGLE", "KAKAO", "NAVER"})
    private SocialType socialType;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Size(min = 2, max = 50, message = "이름은 최소 2자에서 최대 50자까지 입력 가능합니다.")
    @Schema(description = "사용자의 이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "프로필 이미지 주소는 필수 입력값입니다.")
    @Schema(description = "사용자의 프로필 이미지 URL", example = "https://example.com/profile.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    private String profileImage;

    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    @Schema(description = "사용자의 생년월일", example = "1990-01-01", requiredMode = RequiredMode.NOT_REQUIRED)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(min = 3, max = 20, message = "닉네임은 최소 3자에서 최대 20자까지 입력 가능합니다.")
    @Schema(description = "사용자의 닉네임 (3~20자)", example = "cooluser123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickname;

    public User toEntity() {

        return User.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .profileImage(profileImage)
                .birth(birth)
                .role(Role.USER)
                .password(UUID.randomUUID().toString())
                .socialType(socialType)
                .build();
    }
}

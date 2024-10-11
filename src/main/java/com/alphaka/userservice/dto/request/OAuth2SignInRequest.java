package com.alphaka.userservice.dto.request;

import com.alphaka.userservice.entity.Role;
import com.alphaka.userservice.entity.SocialType;
import com.alphaka.userservice.entity.User;
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
    String email;

    @NotNull(message = "소셜타입은 필수 입력값입니다.")
    SocialType socialType;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Size(min = 2, max = 50, message = "이름은 최소 2자에서 최대 50자까지 입력 가능합니다.")
    private String name;

    @NotBlank(message = "프로필 이미지 주소는 필수 입력값입니다.")
    private String profileImage;

    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(min = 3, max = 20, message = "닉네임은 최소 3자에서 최대 20자까지 입력 가능합니다.")
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

package com.alphaka.userservice.dto.request;


import com.alphaka.userservice.entity.Preference;
import com.alphaka.userservice.entity.Role;
import com.alphaka.userservice.entity.SocialType;
import com.alphaka.userservice.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
public class UserSignUpRequest {

    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Size(min = 2, max = 50, message = "이름은 최소 2자에서 최대 50자까지 입력 가능합니다.")
    private String name;

    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10~11자리 숫자만 입력 가능합니다.")
    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    private String phoneNumber;

    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    @NotNull(message = "생년월일은 필수 입력값입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(min = 3, max = 20, message = "닉네임은 최소 3자에서 최대 20자까지 입력 가능합니다.")
    private String nickname;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,}$",
            message = "비밀번호는 최소 1개의 숫자, 문자, 특수 문자를 포함해야 합니다.")
    private String password;

    public User toEntity() {
        Preference pre = Preference.builder()
                .build();

        return User.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .birth(birth)
                .phoneNumber(phoneNumber)
                .role(Role.USER)
                .password(password)
                .socialType(SocialType.EMAIL)
                .preference(pre)
                .build();
    }
}

package com.alphaka.userservice.dto.response;

import com.alphaka.userservice.entity.Gender;
import com.alphaka.userservice.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsResponse {

    @Schema(example = "길동")
    private String name;
    @Schema(example = "gildong12")
    private String nickname;
    @Schema(example = "user@example.com")
    private String email;
    @Schema(example = "01012345678")
    private String phoneNumber;
    @Schema(example = "안녕하세요~")
    private String profileDescription;
    @Schema(example = "MALE")
    private Gender gender;
    @Schema(example = "1990-01-01")
    private LocalDate birth;


    public static UserDetailsResponse fromUser(User user) {
        return UserDetailsResponse.builder()
                .name(user.getName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .profileDescription(user.getProfileDescription())
                .gender(user.getGender())
                .birth(user.getBirth())
                .build();
    }
}

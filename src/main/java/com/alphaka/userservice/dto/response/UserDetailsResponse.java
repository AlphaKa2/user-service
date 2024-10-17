package com.alphaka.userservice.dto.response;

import com.alphaka.userservice.entity.Gender;
import com.alphaka.userservice.entity.User;
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

    private String name;
    private String nickname;
    private String email;
    private String phoneNumber;
    private String profileDescription;
    private Gender gender;
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

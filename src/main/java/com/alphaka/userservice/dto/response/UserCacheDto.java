package com.alphaka.userservice.dto.response;

import com.alphaka.userservice.entity.Gender;
import com.alphaka.userservice.entity.Role;
import com.alphaka.userservice.entity.SocialType;
import com.alphaka.userservice.entity.TripMBTI;
import com.alphaka.userservice.entity.User;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCacheDto {

    private Long id;
    private String email;
    private String nickname;
    private String name;
    private String password;
    private String phoneNumber;
    private LocalDate birth;
    private TripMBTI mbti = TripMBTI.NONE;
    private String mbtiDescription;
    private Gender gender;
    private String profileImage;
    private String profileDescription;
    private Role role;
    private SocialType socialType;
    private boolean isActive;

    public static UserCacheDto fromUser(User user) {

        return UserCacheDto
                .builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .password(user.getPassword())
                .birth(user.getBirth())
                .mbti(user.getMbti())
                .mbtiDescription(user.getMbti().getDescription())
                .gender(user.getGender())
                .profileImage(user.getProfileImage())
                .profileDescription(user.getProfileDescription())
                .role(user.getRole())
                .socialType(user.getSocialType())
                .isActive(user.isActive())
                .build();
    }
}

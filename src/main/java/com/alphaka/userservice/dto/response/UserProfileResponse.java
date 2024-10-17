package com.alphaka.userservice.dto.response;

import com.alphaka.userservice.entity.TripMBTI;
import com.alphaka.userservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    String profileImage;
    String nickname;
    int followerCount;
    int followingCount;
    TripMBTI mbti;
    String mbtiDescription;
    String profileDescription;


    public static UserProfileResponse fromUser(User user) {
        return UserProfileResponse.builder()
                .profileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .followerCount(user.getFollowers().size())
                .followingCount(user.getFollowing().size())
                .mbti(user.getMbti())
                .mbtiDescription(user.getMbti().getDescription())
                .profileDescription(user.getProfileDescription())
                .build();
    }
}

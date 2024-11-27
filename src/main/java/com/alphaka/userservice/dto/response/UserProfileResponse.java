package com.alphaka.userservice.dto.response;

import com.alphaka.userservice.entity.TripMBTI;
import com.alphaka.userservice.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(example = "/img/default")
    String profileImage;
    @Schema(example = "gildong12")
    String nickname;
    @Schema(example = "85")
    int followerCount;
    @Schema(example = "110")
    int followingCount;
    @Schema(example = "ABLJ")
    TripMBTI mbti;
    @Schema(example = "체계적 활동파")
    String mbtiDescription;
    @Schema(example = "안녕하세요~")
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

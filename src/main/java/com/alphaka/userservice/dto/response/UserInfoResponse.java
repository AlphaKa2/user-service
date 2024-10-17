package com.alphaka.userservice.dto.response;

import com.alphaka.userservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private Long userId;
    private String nickname;
    private String profileImage;

    public static UserInfoResponse fromUser(User user) {
        return new UserInfoResponse(user.getId(), user.getNickname(), user.getProfileImage());
    }
}

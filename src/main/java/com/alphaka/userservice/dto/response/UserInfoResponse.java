package com.alphaka.userservice.dto.response;

import com.alphaka.userservice.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    @Schema(example = "1")
    private Long userId;
    @Schema(example = "ImUser1")
    private String nickname;
    @Schema(example = "/img/default")
    private String profileImage;

    public static UserInfoResponse fromUser(User user) {
        return new UserInfoResponse(user.getId(), user.getNickname(), user.getProfileImage());
    }
}

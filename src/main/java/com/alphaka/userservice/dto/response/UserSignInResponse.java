package com.alphaka.userservice.dto.response;

import com.alphaka.userservice.entity.Role;
import com.alphaka.userservice.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSignInResponse {

    @Schema(example = "1")
    private Long id;
    @Schema(example = "/img/default")
    private String profileImage;
    @Schema(example = "userOne")
    private String nickname;
    @Schema(example = "USER")
    private Role role;
    @Schema(example = "P@ssw0rd!")
    private String password;

    public static UserSignInResponse userSignInResponseFromUser(User user) {
        return new UserSignInResponse(user.getId(), user.getProfileImage(), user.getNickname(),
                user.getRole(), null);
    }

    public static UserSignInResponse userSignInResponseWithPasswordFromUser(User user) {
        return new UserSignInResponse(user.getId(), user.getProfileImage(), user.getNickname(),
                user.getRole(), user.getPassword());
    }

}

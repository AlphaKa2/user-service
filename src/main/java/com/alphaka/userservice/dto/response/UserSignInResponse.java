package com.alphaka.userservice.dto.response;

import com.alphaka.userservice.entity.Role;
import com.alphaka.userservice.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
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

    private Long id;
    private String profileImage;
    private String nickname;
    private Role role;
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

package com.alphaka.userservice.dto.response;

import com.alphaka.userservice.entity.Role;
import com.alphaka.userservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserSignInResponse {

    private String email;
    private Role role;

    public static UserSignInResponse fromEntity(User user) {
        return new UserSignInResponse(user.getEmail(), user.getRole());
    }

    public static UserSignInResponse errorResponse() {
        return new UserSignInResponse(null, null);
    }
}

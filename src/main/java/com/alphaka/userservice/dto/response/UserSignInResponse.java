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

    private String email;
    private Role role;
    private String password;

    public static UserSignInResponse userSignInResponse(User user) {
        return new UserSignInResponse(user.getEmail(), user.getRole(), null);
    }

    public static UserSignInResponse userSignInResponseWithPassword(User user) {
        return new UserSignInResponse(user.getEmail(), user.getRole(), user.getPassword());
    }

    public static UserSignInResponse errorResponse() {
        return new UserSignInResponse(null, null, null);
    }
}

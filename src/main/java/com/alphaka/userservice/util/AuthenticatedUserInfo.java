package com.alphaka.userservice.util;

import com.alphaka.userservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticatedUserInfo {

    private Long id;
    private String profileImage;
    private String nickname;
    private Role role;

}

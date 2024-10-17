package com.alphaka.userservice.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserInfoHeader {

    AUTHENTICATED_USER_ID_HEADER("X-User-Id"),
    AUTHENTICATED_USER_ROLE_HEADER("X-User-Role"),
    AUTHENTICATED_USER_PROFILE_HEADER("X-User-Profile"),
    AUTHENTICATED_USER_NICKNAME_HEADER("X-User-Nickname");

    private final String name;

}

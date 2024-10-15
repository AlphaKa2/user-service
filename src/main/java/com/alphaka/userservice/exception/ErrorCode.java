package com.alphaka.userservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    EMAIL_DUPLICATION(HttpStatus.CONFLICT.value(), "USR015", "이미 사용중인 이메일입니다."),
    INVALID_EMAIL_OR_PASSWORD(HttpStatus.BAD_REQUEST.value(), "USR013", "이메일 혹은 비밀번호가 맞지 않습니다."),
    INVALID_FOLLOW_REQUEST(HttpStatus.BAD_REQUEST.value(), "USR009", "팔로우를 할 수 없습니다."),
    INVALID_UNFOLLOW_REQUEST(HttpStatus.BAD_REQUEST.value(), "USR009", "언팔로우를 할 수 없습니다."),
    INVALID_USER_DETAILS_UPDATE_REQUEST(HttpStatus.BAD_REQUEST.value(), "USR009", "수정이 불가능합니다."),
    NICKNAME_DUPLICATION(HttpStatus.CONFLICT.value(), "USR007", "이미 사용중인 닉네임입니다."),
    UNAUTHENTICATED_USER_REQUEST(HttpStatus.UNAUTHORIZED.value(), "USR016", "인증되지 않은 요청입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "USR018", "존재하지 않는 사용자입니다.");
    private final int status;
    private final String code;
    private final String message;
}

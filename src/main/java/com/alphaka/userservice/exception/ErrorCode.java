package com.alphaka.userservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_EMAIL_OR_PASSWORD(HttpStatus.BAD_REQUEST.value(), "USR013", "이메일 혹은 비밀번호가 맞지 않습니다."),
    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST.value(), "USR015", "이미 사용중인 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "USR018", "존재하지 않는 사용자입니다.");

    private final int status;
    private final String code;
    private final String message;
}

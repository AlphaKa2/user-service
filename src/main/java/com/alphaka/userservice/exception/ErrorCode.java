package com.alphaka.userservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    DESERIALIZATION_FAILURE(HttpStatus.BAD_REQUEST.value(), "USR009", "읽을 수 없는 요청입니다."),
    EMAIL_DUPLICATION(HttpStatus.CONFLICT.value(), "USR015", "이미 사용중인 이메일입니다."),
    GENERATING_PRESIGEND_URL_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SYS001", "URL을 생성할 수 없습니다."),
    INVALID_EMAIL_OR_PASSWORD(HttpStatus.BAD_REQUEST.value(), "USR013", "이메일 혹은 비밀번호가 맞지 않습니다."),
    INVALID_FOLLOW_REQUEST(HttpStatus.BAD_REQUEST.value(), "USR009", "팔로우를 할 수 없습니다."),
    INVALID_MBTI_REQUEST(HttpStatus.BAD_REQUEST.value(), "USR009", "알 수 없는 여행 MBTI입니다."),
    INVALID_PROFILE_IMAGE_URL(HttpStatus.BAD_REQUEST.value(), "USR009", "유효하지 않은 URL입니다."),
    INVALID_SMS_CONFIRMATION_TOKEN(HttpStatus.UNAUTHORIZED.value(), "USR016", "유효하지 않은 SMS 검증 토큰입니다."),
    INVALID_UNFOLLOW_REQUEST(HttpStatus.BAD_REQUEST.value(), "USR009", "언팔로우를 할 수 없습니다."),
    INVALID_USER_DETAILS_UPDATE_REQUEST(HttpStatus.BAD_REQUEST.value(), "USR009", "수정이 불가능합니다."),
    NICKNAME_DUPLICATION(HttpStatus.CONFLICT.value(), "USR007", "이미 사용중인 닉네임입니다."),
    UNAUTHENTICATED_USER_REQUEST(HttpStatus.UNAUTHORIZED.value(), "USR016", "인증되지 않은 요청입니다."),
    UNAUTHORIZED_ACCESS_REQUEST(HttpStatus.UNAUTHORIZED.value(), "USR016", "권한이 없습니다."),
    UNCHANGED_NEW_PASSWORD(HttpStatus.BAD_REQUEST.value(), "USR006", "기존 비밀번호와 일치합니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "USR018", "존재하지 않는 사용자입니다."),
    VALIDATION_FAILURE(HttpStatus.BAD_REQUEST.value(), "USR009", "검증이 실패하였습니다."),
    WRONG_PREVIOUS_PASSWORD(HttpStatus.BAD_REQUEST.value(), "USR005", "틀린 비밀번호입니다.");
    private final int status;
    private final String code;
    private final String message;

}

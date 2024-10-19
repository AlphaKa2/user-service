package com.alphaka.userservice.exception.custom;

import com.alphaka.userservice.exception.ErrorCode;

public class InvalidMbtiRequestException extends CustomException {

    public InvalidMbtiRequestException() {
        super(ErrorCode.INVALID_MBTI_REQUEST);
    }
}

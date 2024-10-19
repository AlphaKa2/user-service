package com.alphaka.userservice.exception.custom;

import com.alphaka.userservice.exception.ErrorCode;

public class InvalidEmailOrPasswordException extends CustomException {

    public InvalidEmailOrPasswordException() {
        super(ErrorCode.INVALID_EMAIL_OR_PASSWORD);
    }
}

package com.alphaka.userservice.exception.custom;

import com.alphaka.userservice.exception.ErrorCode;

public class UnchangedNewPasswordException extends CustomException {

    public UnchangedNewPasswordException() {
        super(ErrorCode.UNCHANGED_NEW_PASSWORD);
    }
}

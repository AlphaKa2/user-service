package com.alphaka.userservice.exception.custom;

import com.alphaka.userservice.exception.ErrorCode;

public class WrongPreviousPasswordException extends CustomException {

    public WrongPreviousPasswordException() {
        super(ErrorCode.WRONG_PREVIOUS_PASSWORD);
    }
}

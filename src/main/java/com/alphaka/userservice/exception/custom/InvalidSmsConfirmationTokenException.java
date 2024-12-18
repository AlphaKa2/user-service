package com.alphaka.userservice.exception.custom;

import com.alphaka.userservice.exception.ErrorCode;

public class InvalidSmsConfirmationTokenException extends CustomException {

    public InvalidSmsConfirmationTokenException() {
        super(ErrorCode.INVALID_SMS_CONFIRMATION_TOKEN);
    }
}

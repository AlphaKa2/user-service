package com.alphaka.userservice.exception.custom;

import com.alphaka.userservice.exception.ErrorCode;

public class EmailDuplicationException extends CustomException {

    public EmailDuplicationException() {
        super(ErrorCode.EMAIL_DUPLICATION);
    }
}

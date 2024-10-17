package com.alphaka.userservice.exception.custom;

import com.alphaka.userservice.exception.ErrorCode;

public class NicknameDuplicationException extends CustomException {

    public NicknameDuplicationException() {
        super(ErrorCode.NICKNAME_DUPLICATION);
    }
}

package com.alphaka.userservice.exception.custom;

import com.alphaka.userservice.exception.ErrorCode;

public class UnatuhenticatedUserRequestException extends CustomException {

    public UnatuhenticatedUserRequestException() {
        super(ErrorCode.UNAUTHENTICATED_USER_REQUEST);
    }
}

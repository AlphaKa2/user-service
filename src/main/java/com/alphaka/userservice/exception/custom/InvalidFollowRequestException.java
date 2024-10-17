package com.alphaka.userservice.exception.custom;

import com.alphaka.userservice.exception.ErrorCode;

public class InvalidFollowRequestException extends CustomException {

    public InvalidFollowRequestException() {
        super(ErrorCode.INVALID_FOLLOW_REQUEST);
    }
}

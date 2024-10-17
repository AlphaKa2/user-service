package com.alphaka.userservice.exception.custom;

import com.alphaka.userservice.exception.ErrorCode;

public class InvalidUnfollowRequestException extends CustomException {

    public InvalidUnfollowRequestException() {
        super(ErrorCode.INVALID_UNFOLLOW_REQUEST);
    }
}

package com.alphaka.userservice.exception.custom;

import com.alphaka.userservice.exception.ErrorCode;

public class InvalidUserDetailsUpdateReqeustException extends CustomException {

    public InvalidUserDetailsUpdateReqeustException() {
        super(ErrorCode.INVALID_USER_DETAILS_UPDATE_REQUEST);
    }
}

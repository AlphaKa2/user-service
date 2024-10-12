package com.alphaka.userservice.exception.custom;

import com.alphaka.userservice.exception.ErrorCode;

public class UserNotFoundException extends CustomException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}

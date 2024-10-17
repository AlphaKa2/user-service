package com.alphaka.userservice.exception.custom;

import com.alphaka.userservice.exception.ErrorCode;

public class UnauthorizedAccessReqeust extends CustomException {

    public UnauthorizedAccessReqeust() {
        super(ErrorCode.UNAUTHORIZED_ACCESS_REQUEST);
    }
}

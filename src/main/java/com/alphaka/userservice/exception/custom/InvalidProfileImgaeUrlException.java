package com.alphaka.userservice.exception.custom;

import com.alphaka.userservice.exception.ErrorCode;

public class InvalidProfileImgaeUrlException extends CustomException {

    public InvalidProfileImgaeUrlException() {
        super(ErrorCode.INVALID_PROFILE_IMAGE_URL);
    }
}

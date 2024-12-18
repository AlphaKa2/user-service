package com.alphaka.userservice.swagger.annotation;

import com.alphaka.userservice.exception.ErrorCode;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorResponseExamples {

    ErrorCode[] value();
    String[] name();
    String[] description();
}

package com.alphaka.userservice.swagger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.http.HttpStatus;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiSuccessResponseExample {
    Class<?> responseClass();

    HttpStatus status();

    boolean data();

    Class<?> genericType() default Void.class;
}

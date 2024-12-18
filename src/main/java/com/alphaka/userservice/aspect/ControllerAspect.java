package com.alphaka.userservice.aspect;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class ControllerAspect {

    @Pointcut("execution(* com.alphaka.userservice.controller.*.*(..) )")
    public void controllerAdvice() {
    }

    @Before("controllerAdvice()")
    public void requestLogging(JoinPoint joinPoint) {

        // 현재 HTTP 요청
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // UUID 값 생성 후 MDC에 저장
            String tracingId = UUID.randomUUID().toString();
            MDC.put("trace.id", tracingId);

            // 요청 URL과 메서드 로그 생성
            log.info("REQUEST ::: PATH: {} | METHOD: {}",
                    request.getRequestURI(),
                    request.getMethod());
        }
    }

    @AfterReturning(pointcut = "controllerAdvice()", returning = "returnValue")
    public void responseLogging(JoinPoint joinPoint, Object returnValue) {

        // 현재 HTTP 응답
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletResponse response = attributes.getResponse();

            if (response != null) {
                log.info("RESPONSE ::: STATUS_CODE: {} | RESULT: {}",
                        response.getStatus(),
                        returnValue);
            }
        }

        // MDC 정리
        MDC.clear();
    }


}

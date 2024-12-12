package com.alphaka.userservice.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CacheLoggingAspect {

    private final CacheManager cacheManager;

    @Around("@annotation(cacheable)")
    public Object logCacheable(ProceedingJoinPoint joinPoint, Cacheable cacheable) throws Throwable {
        String cacheName = cacheable.value()[0];
        String methodName = joinPoint.getSignature().toShortString();

        Cache cache = cacheManager.getCache(cacheName);
        Object key = joinPoint.getArgs()[0];

        log.info("키({})로 캐시({})에 대한 캐시 미스 발생, 메소드({}) 호출", key, cacheName, methodName);

        Object result = joinPoint.proceed();
        try {
            log.info("메소드 반환값으로 캐시 초기화 시도");
            cache.put(key, result);
            log.info("캐시 초기화 완료");
        } catch (Exception e) {
            log.error("레디스에 접근할 수 없습니다.", e.getMessage());
        }
        return result;
    }
}


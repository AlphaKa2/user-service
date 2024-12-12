package com.alphaka.userservice.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

@Slf4j
public class RedisCacheExceptionHandler implements CacheErrorHandler {
    // 캐시에 접근이 불가능할 땐, DB에 쿼리를 날려 조회하기 위해 CacheErrorHandler에선 로그만 남기고 따로 예외를 던지지 않는다.
    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.error("캐시 Get Error");
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.error("캐시 Put Error");
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.error("캐시 Evict Error");
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.error("캐시 Clear Error");
    }
}

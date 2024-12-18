package com.alphaka.userservice.config;

import com.alphaka.userservice.exception.handler.RedisCacheExceptionHandler;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    public static final String USER_CACHE = "userService:cache:user";
    public static final String USER_FOLLOW_COUNT_CACHE = "userService:cache:follow-count";
    public static final String NICKNAME_TO_ID_CACHE = "userService:cache:nickname-to-id";

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                     GenericJackson2JsonRedisSerializer jsonSerializer) {

        // jsonSerializer
        RedisCacheConfiguration userCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .entryTtl(Duration.ofMinutes(30))
                .disableCachingNullValues();

        // userID 매핑 데이터 캐시 설정 (Long 타입 Serializer 사용)
        RedisCacheConfiguration userIdMappingCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericToStringSerializer<>(Long.class)))
                .entryTtl(Duration.ofMinutes(30))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put(USER_CACHE, userCacheConfig);
        cacheConfigurations.put(USER_FOLLOW_COUNT_CACHE, userCacheConfig);
        cacheConfigurations.put(NICKNAME_TO_ID_CACHE, userIdMappingCacheConfig);


        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(userCacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new RedisCacheExceptionHandler();
    }
}

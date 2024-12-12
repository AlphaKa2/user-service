package com.alphaka.userservice.service;

import com.alphaka.userservice.config.CacheConfig;
import com.alphaka.userservice.dto.response.FollowCountDto;
import com.alphaka.userservice.dto.response.UserCacheDto;
import com.alphaka.userservice.entity.User;
import com.alphaka.userservice.exception.custom.UserNotFoundException;
import com.alphaka.userservice.repository.FollowRepository;
import com.alphaka.userservice.repository.UserRepository;
import javax.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserCacheService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Resource(name = "userCacheService")
    UserCacheService self;

    @Cacheable(value = CacheConfig.USER_CACHE, key = "#p0", unless = "#result == null")
    public UserCacheDto getUserByIdOrThrowUsingCache(Long userId) {
        return UserCacheDto.fromUser(
                userRepository.findById(userId)
                        .orElseThrow(() -> {
                            log.error("존재하지 않는 사용자입니다. {}", userId);
                            return new UserNotFoundException();
                        })
        );
    }

    @Cacheable(value = CacheConfig.USER_FOLLOW_COUNT_CACHE, key = "#p0", unless = "#result == null")
    public FollowCountDto getFollowCountByIdUsingCache(Long userId) {
        Integer followingCount = followRepository.countFollowingByUserId(userId);
        Integer followerCount = followRepository.countFollowerByUserId(userId);

        return FollowCountDto.builder()
                .followingCount(followingCount)
                .followerCount(followerCount)
                .build();
    }

    public UserCacheDto getUserByNicknameOrThrowUsingCache(String nickname) {
        Long userId = self.getUserIdByNicknameOrThrowUsingCache(nickname);
        return self.getUserByIdOrThrowUsingCache(userId);
    }

    @CachePut(value = CacheConfig.USER_CACHE, key = "#result.id")
    public UserCacheDto updateUserCache(User user) {
        return UserCacheDto.fromUser(user);
    }

    @Cacheable(value = CacheConfig.NICKNAME_TO_ID_CACHE, key = "#p0", unless = "#result == null ")
    public Long getUserIdByNicknameOrThrowUsingCache(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 사용자입니다. {}", nickname);
                    return new UserNotFoundException();
                });

        return user.getId();
    }

}

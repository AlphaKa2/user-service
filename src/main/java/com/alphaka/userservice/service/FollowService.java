package com.alphaka.userservice.service;

import com.alphaka.userservice.dto.response.UserInfoResponse;
import com.alphaka.userservice.entity.Follow;
import com.alphaka.userservice.entity.User;
import com.alphaka.userservice.repository.FollowRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final UserService userService;

    @Transactional
    public Optional<Follow> follow(Long userId, Long targetUserId) {

        Optional<User> maybeTargetUser = userService.findUserById(targetUserId);

        if (userId.equals(targetUserId) || maybeTargetUser.isEmpty()) {
            return Optional.empty();
        }

        User user = userService.findUserById(userId).get();
        User targetUser = maybeTargetUser.get();

        Optional<Follow> maybeFollow = followRepository.findByFollowerAndFollowed(user, targetUser);

        // 이미 팔로우를 하고 있는 경우
        if (maybeFollow.isPresent()) {
            return Optional.empty();
        }

        Follow follow = Follow.builder()
                .follower(user)
                .followed(targetUser)
                .build();

        followRepository.save(follow);

        user.getFollowing().add(follow);
        targetUser.getFollowers().add(follow);

        return Optional.of(follow);
    }

    @Transactional
    public Optional<Follow> unfollow(Long userId, Long targetUserId) {

        Optional<User> maybeTargetUser = userService.findUserById(targetUserId);

        if (userId.equals(targetUserId) || maybeTargetUser.isEmpty()) {
            return Optional.empty();
        }

        User user = userService.findUserById(userId).get();
        User targetUser = maybeTargetUser.get();

        Optional<Follow> maybeFollow = followRepository.findByFollowerAndFollowed(user, targetUser);

        // 두 유저 간의 팔로우 기록이 없는 경우
        if (maybeFollow.isEmpty()) {
            return Optional.empty();
        }

        Follow follow = maybeFollow.get();

        followRepository.delete(follow);

        user.getFollowing().remove(follow);
        targetUser.getFollowers().remove(follow);

        return Optional.of(follow);
    }


    //해당 사용자가 팔로우하는 유저들
    public List<UserInfoResponse> followings(Long userId) {

        Optional<User> maybeUser = userService.findUserById(userId);

        if (maybeUser.isEmpty()) {
            return null;
        }

        return followRepository.findFollowingsByUserId(userId);
    }

    //해당 사용자를 팔로우하는 유저들
    public List<UserInfoResponse> followers(Long userId) {

        Optional<User> maybeUser = userService.findUserById(userId);

        if (maybeUser.isEmpty()) {
            return null;
        }

        return followRepository.findFollowersByUserId(userId);
    }



}

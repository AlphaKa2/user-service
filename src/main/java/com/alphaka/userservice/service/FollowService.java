package com.alphaka.userservice.service;

import com.alphaka.userservice.dto.response.UserInfoResponse;
import com.alphaka.userservice.entity.Follow;
import com.alphaka.userservice.entity.User;
import com.alphaka.userservice.exception.custom.InvalidFollowRequestException;
import com.alphaka.userservice.exception.custom.InvalidUnfollowRequestException;
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
    public void follow(Long userId, Long targetUserId) {

        User targetUser = userService.findUserById(targetUserId);

        if (userId.equals(targetUserId)) {
            throw new InvalidFollowRequestException();
        }

        User user = userService.findUserById(userId);

        Optional<Follow> maybeFollow = followRepository.findByFollowerAndFollowed(user, targetUser);

        // 이미 팔로우를 하고 있는 경우
        if (maybeFollow.isPresent()) {
            throw new InvalidFollowRequestException();
        }

        Follow follow = Follow.builder()
                .follower(user)
                .followed(targetUser)
                .build();

        followRepository.save(follow);

        user.getFollowing().add(follow);
        targetUser.getFollowers().add(follow);
    }

    @Transactional
    public void unfollow(Long userId, Long targetUserId) {

        User targetUser = userService.findUserById(targetUserId);

        if (userId.equals(targetUserId)) {
            throw new InvalidUnfollowRequestException();
        }

        User user = userService.findUserById(userId);

        // 두 유저 간의 팔로우 기록이 없는 경우 예외
        Follow follow = followRepository.findByFollowerAndFollowed(user, targetUser)
                .orElseThrow(InvalidUnfollowRequestException::new);

        followRepository.delete(follow);

        user.getFollowing().remove(follow);
        targetUser.getFollowers().remove(follow);
    }


    //해당 사용자가 팔로우하는 유저들
    public List<UserInfoResponse> followings(Long userId) {

        User user = userService.findUserById(userId);

        return followRepository.findFollowingsByUserId(user.getId());
    }

    //해당 사용자를 팔로우하는 유저들
    public List<UserInfoResponse> followers(Long userId) {

        User user = userService.findUserById(userId);

        return followRepository.findFollowersByUserId(user.getId());
    }


}

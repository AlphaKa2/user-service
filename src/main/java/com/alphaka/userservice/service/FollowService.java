package com.alphaka.userservice.service;

import com.alphaka.userservice.entity.Follow;
import com.alphaka.userservice.entity.User;
import com.alphaka.userservice.repository.FollowRepository;
import com.alphaka.userservice.util.AuthenticatedUserInfo;
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

        if (isAlreadyFollowing(user, targetUser)) {
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

    private boolean isAlreadyFollowing(User follower, User followed) {
        return followRepository.existsByFollowerAndFollowed(follower, followed);
    }

}

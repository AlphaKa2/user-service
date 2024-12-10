package com.alphaka.userservice.service;

import com.alphaka.userservice.dto.response.UserInfoWithFollowStatusResponse;
import com.alphaka.userservice.entity.Follow;
import com.alphaka.userservice.entity.User;
import com.alphaka.userservice.exception.custom.InvalidFollowRequestException;
import com.alphaka.userservice.exception.custom.InvalidUnfollowRequestException;
import com.alphaka.userservice.repository.FollowRepository;
import com.alphaka.userservice.util.UserInfoHeader;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final UserService userService;

    @Transactional
    public void follow(Long userId, Long targetUserId) {

        User targetUser = userService.getUserByIdOrThrow(targetUserId);

        if (userId.equals(targetUserId)) {
            log.error("유저 자신을 팔로우할 수 없습니다.");
            throw new InvalidFollowRequestException();
        }

        User user = userService.getUserByIdOrThrow(userId);

        Optional<Follow> maybeFollow = followRepository.findByFollowerAndFollowed(user.getId(), targetUser.getId());

        // 이미 팔로우를 하고 있는 경우
        if (maybeFollow.isPresent()) {
            log.error("이미 팔로우하고 있는 유저입니다.");
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

        User targetUser = userService.getUserByIdOrThrow(targetUserId);

        if (userId.equals(targetUserId)) {
            log.error("유저 자신을 언팔로우할 수 없습니다.");
            throw new InvalidUnfollowRequestException();
        }

        User user = userService.getUserByIdOrThrow(userId);

        // 두 유저 간의 팔로우 기록이 없는 경우 예외
        Follow follow = followRepository.findByFollowerAndFollowed(user.getId(), targetUser.getId())
                .orElseThrow(InvalidUnfollowRequestException::new);

        followRepository.delete(follow);

        user.getFollowing().remove(follow);
        targetUser.getFollowers().remove(follow);
    }

    public List<UserInfoWithFollowStatusResponse> followingsWithStatus(Long targetUserId, HttpServletRequest request) {

        User targetUser = userService.getUserByIdOrThrow(targetUserId);

        String id = request.getHeader(UserInfoHeader.AUTHENTICATED_USER_ID_HEADER.getName());
        if (id == null) {
            log.info("로그인하지 않은 사용자의 요청");
            return followRepository.findFollowingsByUserId(targetUser.getId());
        }

        Long requestUserId = Long.valueOf(id);
        log.info("로그인한 사용자 {}의 요청", requestUserId);
        User requestUser = userService.getUserByIdOrThrow(requestUserId);

        log.info("팔로우 여부도 함께 조회");
        return followRepository.findFollowingsWithFollowStatusByRequestUserIdAndTargetUserId(requestUserId, targetUserId);
    }

    public List<UserInfoWithFollowStatusResponse> followersWithStatus(Long targetUserId, HttpServletRequest request) {

        User targetUser = userService.getUserByIdOrThrow(targetUserId);

        String id = request.getHeader(UserInfoHeader.AUTHENTICATED_USER_ID_HEADER.getName());
        if (id == null) {
            log.info("로그인하지 않은 사용자의 요청");
            return followRepository.findFollowersByUserId(targetUser.getId());
        }

        Long requestUserId = Long.valueOf(id);
        log.info("로그인한 사용자 {}의 요청", requestUserId);
        User requestUser = userService.getUserByIdOrThrow(requestUserId);

        log.info("팔로우 여부도 함께 조회");
        return followRepository.findFollowersWithFollowStatusByRequestUserIdAndTargetUserId(requestUserId, targetUserId);
    }

    public boolean isFollowing(Long userId, Long targetUserId) {
        User user = userService.getUserByIdOrThrow(userId);
        User targetUser = userService.getUserByIdOrThrow(targetUserId);

        return followRepository.findByFollowerAndFollowed(user.getId(), targetUser.getId()).isPresent();
    }


}

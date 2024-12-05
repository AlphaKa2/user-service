package com.alphaka.userservice.controller;

import com.alphaka.userservice.dto.response.ApiResponse;
import com.alphaka.userservice.dto.response.UserInfoResponse;
import com.alphaka.userservice.service.FollowService;
import com.alphaka.userservice.util.AuthenticatedUserInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class FollowController implements FollowApi {

    private final FollowService followService;

    @Override
    @PostMapping("/{targetUserId}/following")
    @ResponseBody
    public ApiResponse<String> follow(@PathVariable("targetUserId") Long targetUserId,
                                      AuthenticatedUserInfo authenticatedUserInfo) {
        log.info("유저 {}의 유저 {} 팔로우 요청", authenticatedUserInfo.getId(), targetUserId);
        followService.follow(authenticatedUserInfo.getId(), targetUserId);

        log.info("유저 {}의 유저 {} 팔로우 요청 성공", authenticatedUserInfo.getId(), targetUserId);
        return ApiResponse.createSuccessResponse(HttpStatus.OK.value());
    }

    @Override
    @DeleteMapping("/{targetUserId}/following")
    @ResponseBody
    public ApiResponse<String> unfollow(@PathVariable("targetUserId") Long targetUserId,
                                        AuthenticatedUserInfo authenticatedUserInfo) {
        log.info("유저 {}의 유저 {} 언팔로우 요청", authenticatedUserInfo.getId(), targetUserId);
        followService.unfollow(authenticatedUserInfo.getId(), targetUserId);

        log.info("유저 {}의 유저 {} 언팔로우 요청 성공", authenticatedUserInfo.getId(), targetUserId);
        return ApiResponse.createSuccessResponse(HttpStatus.OK.value());
    }

    //사용자의 타겟 유저 팔로우 여부
    @GetMapping("/follow/status")
    @ResponseBody
    public ApiResponse<Boolean> followStatus(@RequestParam("userId") Long userId,
                                             AuthenticatedUserInfo authenticatedUserInfo) {
        log.info("유저 {}의 유저 {} 팔로우 여부 조회 요청", authenticatedUserInfo.getId(),userId);
        boolean isFollowing = followService.isFollowing(authenticatedUserInfo.getId(), userId);

        log.info("팔로잉 여부:{}", isFollowing);
        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(), isFollowing);
    }

    //사용자의 팔로잉 목록
    @Override
    @GetMapping("/{userId}/following")
    @ResponseBody
    public ApiResponse<List<UserInfoResponse>> following(@PathVariable("userId") Long userId) {
        log.info("유저 {}의 팔로잉 목록 조회 요청", userId);
        List<UserInfoResponse> followings = followService.followings(userId);

        log.info("유저 {}의 팔로잉 목록 조회 요청 성공", userId);
        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(), followings);
    }

    //사용자의 팔로워 목록
    @Override
    @GetMapping("/{userId}/follower")
    @ResponseBody
    public ApiResponse<List<UserInfoResponse>> follower(@PathVariable("userId") Long userId) {
        log.info("유저 {}의 팔로워 목록 조회 요청", userId);
        List<UserInfoResponse> followers = followService.followers(userId);

        log.info("유저 {}의 팔로워 목록 조회 요청 성공", userId);
        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(), followers);
    }


}

package com.alphaka.userservice.controller;

import com.alphaka.userservice.dto.response.ApiResponse;
import com.alphaka.userservice.dto.response.UserInfoResponse;
import com.alphaka.userservice.service.FollowService;
import com.alphaka.userservice.util.AuthenticatedUserInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{targetUserId}/following")
    @ResponseBody
    public ApiResponse<String> follow(@PathVariable("targetUserId") Long targetUserId,
                                      AuthenticatedUserInfo authenticatedUserInfo) {
        followService.follow(authenticatedUserInfo.getId(), targetUserId);

        return ApiResponse.createSuccessResponse(HttpStatus.OK.value());
    }

    @DeleteMapping("/{targetUserId}/following")
    @ResponseBody
    public ApiResponse<String> unfollow(@PathVariable("targetUserId") Long targetUserId,
                                        AuthenticatedUserInfo authenticatedUserInfo) {
        followService.unfollow(authenticatedUserInfo.getId(), targetUserId);

        return ApiResponse.createSuccessResponse(HttpStatus.OK.value());
    }

    //사용자의 팔로잉 목록
    @GetMapping("/{userId}/following")
    @ResponseBody
    public ApiResponse<List<UserInfoResponse>> following(@PathVariable("userId") Long userId) {
        List<UserInfoResponse> followings = followService.followings(userId);

        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(), followings);
    }

    //사용자의 팔로워 목록
    @GetMapping("/{userId}/follower")
    @ResponseBody
    public ApiResponse<List<UserInfoResponse>> follower(@PathVariable("userId") Long userId) {
        List<UserInfoResponse> followers = followService.followers(userId);

        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(), followers);
    }


}

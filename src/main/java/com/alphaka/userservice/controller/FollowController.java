package com.alphaka.userservice.controller;

import com.alphaka.userservice.dto.response.ApiResponse;
import com.alphaka.userservice.dto.response.UserInfoResponse;
import com.alphaka.userservice.entity.Follow;
import com.alphaka.userservice.exception.custom.InvalidFollowRequestException;
import com.alphaka.userservice.exception.custom.InvalidUnfollowRequestException;
import com.alphaka.userservice.exception.custom.UserNotFoundException;
import com.alphaka.userservice.service.FollowService;
import com.alphaka.userservice.util.AuthenticatedUserInfo;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow/{targetUserId}")
    @ResponseBody
    public ApiResponse<String> follow(@PathVariable("targetUserId") Long targetUserId,
                                      AuthenticatedUserInfo authenticatedUserInfo) {
        Optional<Follow> maybeFollow = followService.follow(authenticatedUserInfo.getId(), targetUserId);

        if (maybeFollow.isEmpty()) {
            throw new InvalidFollowRequestException();
        }

        return ApiResponse.createSuccessResponse(HttpStatus.OK.value());
    }

    @PostMapping("/unfollow/{targetUserId}")
    @ResponseBody
    public ApiResponse<String> unfollow(@PathVariable("targetUserId") Long targetUserId,
                                        AuthenticatedUserInfo authenticatedUserInfo) {
        Optional<Follow> maybeFollow = followService.unfollow(authenticatedUserInfo.getId(), targetUserId);

        if (maybeFollow.isEmpty()) {
            throw new InvalidUnfollowRequestException();
        }
        return ApiResponse.createSuccessResponse(HttpStatus.OK.value());
    }

    //사용자의 팔로잉 목록
    @GetMapping("/following/{userId}")
    @ResponseBody
    public ApiResponse<List<UserInfoResponse>> following(@PathVariable("userId") Long userId) {
        List<UserInfoResponse> followings = followService.followings(userId);
        if (followings == null) {
            throw new UserNotFoundException();
        }

        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(), followings);
    }

    //사용자의 팔로워 목록
    @GetMapping("/follower/{userId}")
    @ResponseBody
    public ApiResponse<List<UserInfoResponse>> follower(@PathVariable("userId") Long userId) {
        List<UserInfoResponse> followers = followService.followers(userId);
        if (followers == null) {
            throw new UserNotFoundException();
        }

        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(), followers);
    }


}

package com.alphaka.userservice.controller;

import com.alphaka.userservice.dto.response.ApiResponse;
import com.alphaka.userservice.entity.Follow;
import com.alphaka.userservice.exception.custom.InvalidFollowRequestException;
import com.alphaka.userservice.exception.custom.InvalidUnfollowRequestException;
import com.alphaka.userservice.service.FollowService;
import com.alphaka.userservice.util.AuthenticatedUserInfo;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow/{targetUserId}")
    public ResponseEntity<ApiResponse> follow(@PathVariable("targetUserId") Long targetUserId,
                                              AuthenticatedUserInfo authenticatedUserInfo) {
        Optional<Follow> maybeFollow = followService.follow(authenticatedUserInfo.getId(), targetUserId);

        if (maybeFollow.isEmpty()) {
            throw new InvalidFollowRequestException();
        }

        return ResponseEntity.ok(ApiResponse.createSuccessResponse(200));
    }

    @PostMapping("/unfollow/{targetUserId}")
    public ResponseEntity<ApiResponse> unfollow(@PathVariable("targetUserId") Long targetUserId,
                                                AuthenticatedUserInfo authenticatedUserInfo) {
        Optional<Follow> maybeFollow = followService.unfollow(authenticatedUserInfo.getId(), targetUserId);

        if (maybeFollow.isEmpty()) {
            throw new InvalidUnfollowRequestException();
        }
        return ResponseEntity.ok(ApiResponse.createSuccessResponse(200));
    }
}

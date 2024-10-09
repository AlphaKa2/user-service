package com.alphaka.userservice.controller;

import com.alphaka.userservice.dto.response.ApiResponse;
import com.alphaka.userservice.dto.request.UserSignUpRequest;
import com.alphaka.userservice.entity.User;
import com.alphaka.userservice.service.UserService;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users/join")
    public ApiResponse<String> join(@RequestBody @Valid UserSignUpRequest userSignUpRequest) {
        Optional<User> user = userService.join(userSignUpRequest);

        if (user.isEmpty()) {
            return ApiResponse.createErrorResponseWithExceptions(HttpStatus.BAD_REQUEST.value(), "이미 존재하는 사용자 입니다.");
        }
        return ApiResponse.createSuccessResponse(HttpStatus.CREATED.value());
    }


}

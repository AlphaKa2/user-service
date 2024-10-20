package com.alphaka.userservice.controller;

import com.alphaka.userservice.dto.request.OAuth2SignInRequest;
import com.alphaka.userservice.dto.request.PasswordUpdateRequest;
import com.alphaka.userservice.dto.request.TripMbtiUpdateRequest;
import com.alphaka.userservice.dto.request.UserDetailsUpdateRequest;
import com.alphaka.userservice.dto.request.UserSignInRequest;
import com.alphaka.userservice.dto.response.ApiResponse;
import com.alphaka.userservice.dto.request.UserSignUpRequest;
import com.alphaka.userservice.dto.response.UserDetailsResponse;
import com.alphaka.userservice.dto.response.UserInfoResponse;
import com.alphaka.userservice.dto.response.UserProfileResponse;
import com.alphaka.userservice.dto.response.UserSignInResponse;
import com.alphaka.userservice.entity.User;
import com.alphaka.userservice.exception.custom.EmailDuplicationException;
import com.alphaka.userservice.exception.custom.UserNotFoundException;
import com.alphaka.userservice.service.UserService;
import com.alphaka.userservice.util.AuthenticatedUserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    //accessToken 재발급 시 사용
    @GetMapping("/{userId}")
    public ApiResponse<UserSignInResponse> user(@PathVariable Long userId) {

        User user = userService.findUserById(userId);

        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(),
                UserSignInResponse.userSignInResponseFromUser(user));
    }

    // 자체 회원가입
    @PostMapping("/join")
    public ApiResponse<String> join(@RequestBody @Valid UserSignUpRequest userSignUpRequest) {
        userService.join(userSignUpRequest);

        return ApiResponse.createSuccessResponse(HttpStatus.CREATED.value());
    }


    // 인증 서비스에서 OAuth2 로그인 시
    @PostMapping("/oauth2/signin")
    public ApiResponse<UserSignInResponse> oauth2SignIn(@RequestBody @Valid OAuth2SignInRequest oAuth2SignInRequest) {
        User user = userService.oauth2SignIn(oAuth2SignInRequest);

        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(),
                UserSignInResponse.userSignInResponseFromUser(user));
    }


    // 인증 서비스에서 자체 로그인 시
    @PostMapping("/signin")
    public ApiResponse<UserSignInResponse> signIn(@RequestBody @Valid UserSignInRequest userSignInRequest) {
        User user = userService.signIn(userSignInRequest);

        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(),
                UserSignInResponse.userSignInResponseWithPasswordFromUser(user));
    }


    //닉네임 중복체크, 닉네임이 중복이 아니라면 true 리턴
    @GetMapping("/nickname/{nickname}/exist")
    public ApiResponse<Boolean> nicknameValidation(@PathVariable("nickname") String nickname) {

        userService.checkNicknameDuplication(nickname);

        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(),
                true);
    }

    @GetMapping("/email/{email}/exist")
    public ApiResponse<Boolean> emailValidation(@PathVariable("email") String email) {

        userService.checkEmailDuplication(email);

        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(),
                true);
    }


    @GetMapping("/{userId}/profile")
    public ApiResponse<UserProfileResponse> userProfile(@PathVariable("userId") Long userId) {
        User user = userService.findUserById(userId);

        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(),
                UserProfileResponse.fromUser(user));
    }

    @GetMapping("/info")
    public ApiResponse<UserInfoResponse> userInfoByIdOrNickname(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "nickname", required = false) String nickname) {

        User user = null;
        if (id != null) {
            user = userService.findUserById(id);
        } else if (nickname != null) {
            user = userService.findUserByNickname(nickname);
        } else {
            throw new UserNotFoundException();
        }

        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(),
                UserInfoResponse.fromUser(user));
    }

    @GetMapping("/{userId}/details")
    public ApiResponse<UserDetailsResponse> userDetails(@PathVariable("userId") Long userId) {
        User user = userService.findUserById(userId);

        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(),
                UserDetailsResponse.fromUser(user));
    }

    @PutMapping("/{userId}/details")
    @ResponseBody
    public ApiResponse<String> updateUserDetails(@PathVariable("userId") Long userId,
                                                 @RequestBody @Valid UserDetailsUpdateRequest userDetailsUpdateRequest,
                                                 AuthenticatedUserInfo authenticatedUserInfo) {
        userService.updateUserDetails(userId, userDetailsUpdateRequest, authenticatedUserInfo);

        return ApiResponse.createSuccessResponse(HttpStatus.OK.value());
    }

    @PutMapping("/{userId}/password")
    @ResponseBody
    public ApiResponse<String> updateUserPassword(@PathVariable("userId") Long userId,
                                                  @RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest,
                                                  AuthenticatedUserInfo authenticatedUserInfo) {
        userService.updatePassword(userId, passwordUpdateRequest, authenticatedUserInfo);

        return ApiResponse.createSuccessResponse(HttpStatus.OK.value());
    }

    @PutMapping("/{userId}/mbti")
    @ResponseBody
    public ApiResponse<String> updateUserPassword(@PathVariable("userId") Long userId,
                                                  @RequestBody @Valid TripMbtiUpdateRequest tripMbtiUpdateRequest,
                                                  AuthenticatedUserInfo authenticatedUserInfo) {
        userService.updateMbti(userId, tripMbtiUpdateRequest, authenticatedUserInfo);

        return ApiResponse.createSuccessResponse(HttpStatus.OK.value());
    }

}

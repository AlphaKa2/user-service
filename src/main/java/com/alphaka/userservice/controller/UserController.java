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
import com.alphaka.userservice.service.UserService;
import com.alphaka.userservice.util.AuthenticatedUserInfo;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController implements UserApi {

    private final UserService userService;

    //accessToken 재발급 시 사용
    @Override
    @GetMapping("/{userId}")
    public ApiResponse<UserSignInResponse> user(@PathVariable("userId") Long userId) {

        log.info("인증서비스 accessToken 재발급 위한 유저 {} 정보 조회 요청", userId);
        User user = userService.getUserByIdOrThrow(userId);

        log.info("인증서비스 accessToken 재발급 위한 유저 {} 정보 조회 성공", userId);
        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(),
                UserSignInResponse.userSignInResponseFromUser(user));
    }

    // 자체 회원가입
    @Override
    @PostMapping("/join")
    public ApiResponse<String> join(@RequestBody @Valid UserSignUpRequest userSignUpRequest) {

        log.info("사용자 자체 회원가입 요청");
        userService.join(userSignUpRequest);

        log.info("사용자 자체 회원가입 성공");
        return ApiResponse.createSuccessResponse(HttpStatus.CREATED.value());
    }


    // 인증 서비스에서 OAuth2 로그인 시
    @Override
    @PostMapping("/oauth2/signin")
    public ApiResponse<UserSignInResponse> oauth2SignIn(@RequestBody @Valid OAuth2SignInRequest oAuth2SignInRequest) {
        log.info("사용자 OAuth2 인증 요청");
        User user = userService.oauth2SignIn(oAuth2SignInRequest);

        log.info("사용자 OAuth2 인증 성공");
        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(),
                UserSignInResponse.userSignInResponseFromUser(user));
    }


    // 인증 서비스에서 자체 로그인 시
    @Override
    @PostMapping("/signin")
    public ApiResponse<UserSignInResponse> signIn(@RequestBody @Valid UserSignInRequest userSignInRequest) {
        log.info("사용자 인증 요청");
        User user = userService.signIn(userSignInRequest);

        log.info("사용자 인증 성공");
        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(),
                UserSignInResponse.userSignInResponseWithPasswordFromUser(user));
    }


    //닉네임 중복체크, 닉네임이 중복이 아니라면 true 리턴
    @Override
    @GetMapping("/nickname/{nickname}/exist")
    public ApiResponse<String> nicknameValidation(@PathVariable("nickname") String nickname) {
        log.info("닉네임 중복 검사 요청");
        userService.checkNicknameDuplication(nickname);

        log.info("닉네임 중복 검사 통과");
        return ApiResponse.createSuccessResponse(HttpStatus.OK.value());
    }

    @Override
    @GetMapping("/email/{email}/exist")
    public ApiResponse<String> emailValidation(@PathVariable("email") String email) {
        log.info("이메일 중복 검사 요청");
        userService.checkEmailDuplication(email);

        log.info("이메일 중복 검사 통과");
        return ApiResponse.createSuccessResponse(HttpStatus.OK.value());
    }


    @Override
    @GetMapping("/{userId}/profile")
    public ApiResponse<UserProfileResponse> userProfile(@PathVariable("userId") Long userId) {
        log.info("사용자 프로필 조회 요청");
        User user = userService.getUserByIdOrThrow(userId);

        log.info("사용자 프로필 조회 성공");
        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(),
                UserProfileResponse.fromUser(user));
    }

    @Override
    @GetMapping("/info")
    public ApiResponse<UserInfoResponse> userInfoByIdOrNickname(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "nickname", required = false) String nickname) {

        log.info("블로그 서비스 사용자 정보 조회 요청");

        User user = userService.findUserByIdOrNickname(userId, nickname);

        log.info("블로그 서비스 사용자 정보 조회 요청 성공");
        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(),
                UserInfoResponse.fromUser(user));
    }

    @Override
    @GetMapping("/{userId}/details")
    public ApiResponse<UserDetailsResponse> userDetails(@PathVariable("userId") Long userId) {
        log.info("사용자 상세 정보 조회 요청");
        User user = userService.getUserByIdOrThrow(userId);

        log.info("사용자 상세 정보 조회 성공");
        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(),
                UserDetailsResponse.fromUser(user));
    }

    @Override
    @PutMapping("/{userId}/details")
    @ResponseBody
    public ApiResponse<String> updateUserDetails(@PathVariable("userId") Long userId,
                                                 @RequestBody @Valid UserDetailsUpdateRequest userDetailsUpdateRequest,
                                                 AuthenticatedUserInfo authenticatedUserInfo) {
        log.info("사용자 상세 정보 업데이트 요청");
        userService.updateUserDetails(userId, userDetailsUpdateRequest, authenticatedUserInfo);

        log.info("사용자 상세 정보 업데이트 성공");
        return ApiResponse.createSuccessResponse(HttpStatus.OK.value());
    }

    @Override
    @PutMapping("/{userId}/password")
    @ResponseBody
    public ApiResponse<String> updateUserPassword(@PathVariable("userId") Long userId,
                                                  @RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest,
                                                  AuthenticatedUserInfo authenticatedUserInfo) {
        log.info("사용자 비밀번호 업데이트 요청");
        userService.updatePassword(userId, passwordUpdateRequest, authenticatedUserInfo);

        log.info("사용자 비밀번호 업데이트 성공");
        return ApiResponse.createSuccessResponse(HttpStatus.OK.value());
    }

    @Override
    @PutMapping("/{userId}/mbti")
    @ResponseBody
    public ApiResponse<String> updateUserMbti(@PathVariable("userId") Long userId,
                                              @RequestBody @Valid TripMbtiUpdateRequest tripMbtiUpdateRequest,
                                              AuthenticatedUserInfo authenticatedUserInfo) {
        log.info("사용자 여행 MBTI 업데이트 요청");
        userService.updateMbti(userId, tripMbtiUpdateRequest, authenticatedUserInfo);

        log.info("사용자 여행 MBTI 업데이트 성공");
        return ApiResponse.createSuccessResponse(HttpStatus.OK.value());
    }

    @Override
    @GetMapping
    public ApiResponse<List<UserInfoResponse>> getUserList(@RequestParam("userIds") Set<Long> userIds) {
        log.info("블로그 서비스 사용자들 리스트 정보 조회 요청");
        List<UserInfoResponse> userList = userService.findUsersByIds(userIds);

        log.info("블로그 서비스 사용자들 리스트 정보 조회 성공");
        return ApiResponse.createSuccessResponseWithData(HttpStatus.OK.value(),
                userList);
    }

}

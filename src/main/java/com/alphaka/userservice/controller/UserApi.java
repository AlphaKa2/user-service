package com.alphaka.userservice.controller;


import static com.alphaka.userservice.exception.ErrorCode.*;

import com.alphaka.userservice.dto.request.OAuth2SignInRequest;
import com.alphaka.userservice.dto.request.PasswordResetRequest;
import com.alphaka.userservice.dto.request.PasswordUpdateRequest;
import com.alphaka.userservice.dto.request.ProfileImageUrlUpdateRequest;
import com.alphaka.userservice.dto.request.S3PresignedUrlRequest;
import com.alphaka.userservice.dto.request.TripMbtiUpdateRequest;
import com.alphaka.userservice.dto.request.UserDetailsUpdateRequest;
import com.alphaka.userservice.dto.request.UserSignInRequest;
import com.alphaka.userservice.dto.request.UserSignUpRequest;
import com.alphaka.userservice.dto.response.ApiResponse;
import com.alphaka.userservice.dto.response.S3PresignedUrlResponse;
import com.alphaka.userservice.dto.response.UserDetailsResponse;
import com.alphaka.userservice.dto.response.UserInfoResponse;
import com.alphaka.userservice.dto.response.UserProfileResponse;
import com.alphaka.userservice.dto.response.UserSignInResponse;
import com.alphaka.userservice.swagger.annotation.ApiErrorResponseExamples;
import com.alphaka.userservice.swagger.annotation.ApiSuccessResponseExample;
import com.alphaka.userservice.util.AuthenticatedUserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "유저 API", description = "유저 관련 API")
public interface UserApi {

    @Operation(
            summary = "인증 서비스의 토큰 재발급을 위한 사용자 정보 조회",
            description = "인증 서비스에서 accessToken 재발급을 위해 유저 id로 사용자 정보를 조회하는 API입니다..",
            tags = {"Internal API"},
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "사용자의 고유 식별 ID",
                            required = true,
                            example = "1",
                            in = ParameterIn.PATH, // PathVariable임을 명시
                            schema = @Schema(type = "integer", format = "int64")
                    )
            }
    )
    @ApiSuccessResponseExample(responseClass = UserSignInResponse.class, data = true, status = HttpStatus.OK)
    @ApiErrorResponseExamples(
            value = {USER_NOT_FOUND},
            name = {"사용자 없음"},
            description = {"존재하지 않는 사용자입니다."}
    )
    ApiResponse<UserSignInResponse> user(@PathVariable Long userId);


    @Operation(
            summary = "사용자 자체 회원가입 요청",
            description = "사용자의 자체 회원가입 요청을 처리하는 API입니다.",
            tags = {"External API"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 요청 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserSignUpRequest.class)
                    )
            )
    )
    @ApiSuccessResponseExample(responseClass = String.class, data = false, status = HttpStatus.CREATED)
    @ApiErrorResponseExamples(
            value = {EMAIL_DUPLICATION, NICKNAME_DUPLICATION, INVALID_SMS_CONFIRMATION_TOKEN,
                    DESERIALIZATION_FAILURE, VALIDATION_FAILURE},
            name = {"이메일 중복", "닉네임 중복", "유효하지 않은 SMS 인증 토큰", "역직렬화 실패", "검증 실패"},
            description = {"증복된 이메일입니다.", "닉네임 중복입니다.", "유효하지 않은 SMS 인증 토큰입니다.",
                    "역직렬화에 실패하였습니다.", "검증에 실패하였습니다."}
    )
    ApiResponse<String> join(@RequestBody @Valid UserSignUpRequest userSignUpRequest);

    @Operation(
            summary = "인증 서비스의 소셜 로그인을 위한 사용자 정보 조회",
            description = "인증 서비스에서 소셜 로그인을 처리할 때 필요한 API입니다.",
            tags = {"Internal API"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "소셜 로그인 요청 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OAuth2SignInRequest.class)
                    )
            )
    )
    @ApiSuccessResponseExample(responseClass = UserSignInResponse.class, data = true, status = HttpStatus.OK)
    @ApiErrorResponseExamples(
            value = {EMAIL_DUPLICATION, DESERIALIZATION_FAILURE, VALIDATION_FAILURE},
            name = {"이메일 중복", "역직렬화 실패", "검증 실패"},
            description = {"중복된 이메일입니다.", "역직렬화에 실패하였습니다.", "검증에 실패하였습니다."}
    )
    ApiResponse<UserSignInResponse> oauth2SignIn(@RequestBody @Valid OAuth2SignInRequest oAuth2SignInRequest);

    @Operation(
            summary = "인증 서비스의 자체 로그인을 위한 사용자 정보 조회",
            description = "인증 서비스에서 자체 로그인을 처리할 때 필요한 API입니다.",
            tags = {"Internal API"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "자체 로그인 요청 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserSignInRequest.class)
                    )
            )
    )
    @ApiSuccessResponseExample(responseClass = UserSignInResponse.class, data = true, status = HttpStatus.OK)
    @ApiErrorResponseExamples(
            value = {USER_NOT_FOUND, DESERIALIZATION_FAILURE, VALIDATION_FAILURE},
            name = {"사용자 없음", "역직렬화 실패", "검증 실패"},
            description = {"존재하지 않는 사용자입니다.", "역직렬화에 실패하였습니다.", "검증에 실패하였습니다."}
    )
    ApiResponse<UserSignInResponse> signIn(@RequestBody @Valid UserSignInRequest userSignInRequest);

    @Operation(
            summary = "닉네임 중복 판단",
            description = "닉네임 중복을 판단하는 API 입니다. 중복이라면 예외를 던집니다.",
            tags = {"External API"},
            parameters = {
                    @Parameter(
                            name = "nickname",
                            description = "사용자의 고유한 닉네임",
                            required = true,
                            example = "userA",
                            in = ParameterIn.PATH, // PathVariable임을 명시
                            schema = @Schema(type = "string")
                    )
            }
    )
    @ApiSuccessResponseExample(responseClass = String.class, data = false, status = HttpStatus.OK)
    @ApiErrorResponseExamples(
            value = {NICKNAME_DUPLICATION},
            name = {"닉네임 중복"},
            description = {"중복된 닉네임입니다."}
    )
    ApiResponse<String> nicknameValidation(@PathVariable("nickname") String nickname);

    @Operation(
            summary = "이메일 중복 판단",
            description = "이메일 중복을 판단하는 API 입니다. 중복이라면 예외를 던집니다.",
            tags = {"External API"},
            parameters = {
                    @Parameter(
                            name = "email",
                            description = "사용자의 고유한 이메일",
                            required = true,
                            example = "userA@exmaple.com",
                            in = ParameterIn.PATH, // PathVariable임을 명시
                            schema = @Schema(type = "string")
                    )
            }
    )
    @ApiSuccessResponseExample(responseClass = String.class, data = false, status = HttpStatus.OK)
    @ApiErrorResponseExamples(
            value = {EMAIL_DUPLICATION},
            name = {"이메일 중복"},
            description = {"중복된 이메일입니다."}
    )
    ApiResponse<String> emailValidation(@PathVariable("email") String email);

    @Operation(
            summary = "사용자 프로필 조회",
            description = "사용자의 id를 통해 사용자 프로필을 조회하는 API입니다.",
            tags = {"External API"},
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "사용자의 고유한 ID",
                            required = true,
                            example = "1",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            }
    )
    @ApiSuccessResponseExample(responseClass = UserProfileResponse.class, data = true, status = HttpStatus.OK)
    @ApiErrorResponseExamples(
            value = {USER_NOT_FOUND},
            name = {"사용자 없음"},
            description = {"존재하지 않는 사용자입니다."}
    )
    ApiResponse<UserProfileResponse> userProfile(@PathVariable("userId") Long userId);


    @Operation(
            summary = "사용자 닉네임으로 프로필 조회",
            description = "사용자의 닉네임을 통해 사용자 프로필을 조회하는 API입니다.",
            tags = {"External API"},
            parameters = {
                    @Parameter(
                            name = "nickname",
                            description = "사용자의 고유한 닉네임",
                            required = true,
                            example = "userA",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string")
                    )
            }
    )
    @ApiSuccessResponseExample(responseClass = UserProfileResponse.class, data = true, status = HttpStatus.OK)
    @ApiErrorResponseExamples(
            value = {USER_NOT_FOUND},
            name = {"사용자 없음"},
            description = {"존재하지 않는 사용자입니다."}
    )
    ApiResponse<UserProfileResponse> userProfileByNickname(@PathVariable("nickname") String nickname);

    @Operation(
            summary = "블로그 서비스의 사용자 정보 조회",
            description = "블로그 서비스에서 사용자의 id 혹은 닉네임으로 사용자 정보를 조회하는 API입니다.",
            tags = {"Internal API"},
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "사용자의 고유한 ID",
                            required = false,
                            example = "1",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "integer", format = "int64")
                    ),
                    @Parameter(
                            name = "nickname",
                            description = "사용자의 고유한 닉네임",
                            required = false,
                            example = "userA",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string")
                    )
            }
    )
    @ApiSuccessResponseExample(responseClass = UserInfoResponse.class, data = true, status = HttpStatus.OK)
    @ApiErrorResponseExamples(
            value = {USER_NOT_FOUND},
            name = {"사용자 없음"},
            description = {"존재하지 않는 사용자입니다."}
    )
    ApiResponse<UserInfoResponse> userInfoByIdOrNickname(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "nickname", required = false) String nickname);

    @Operation(
            summary = "사용자 상세 정보 조회",
            description = "사용자의 id를 통해 사용자 상세 정보를 조회하는 API입니다.",
            tags = {"External API"},
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "사용자의 고유한 ID",
                            required = true,
                            example = "1",
                            in = ParameterIn.PATH, // PathVariable임을 명시
                            schema = @Schema(type = "integer", format = "int64")
                    )
            }
    )
    @ApiSuccessResponseExample(responseClass = UserDetailsResponse.class, data = true, status = HttpStatus.OK)
    @ApiErrorResponseExamples(
            value = {USER_NOT_FOUND},
            name = {"사용자 없음"},
            description = {"존재하지 않는 사용자입니다."}
    )
    ApiResponse<UserDetailsResponse> userDetails(@PathVariable("userId") Long userId);

    @Operation(
            summary = "사용자 상세 정보 업데이트",
            description = "사용자 상세 정보 중, 닉네임, 프로필 메시지를 업데이트하는 API입니다. accssToken이 필요합니다.",
            tags = {"External API"},
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "사용자의 고유한 ID",
                            required = true,
                            example = "1",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "사용자 상세 정보 업데이트 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDetailsUpdateRequest.class)
                    )
            )
    )
    @ApiSuccessResponseExample(responseClass = String.class, data = false, status = HttpStatus.OK)
    @ApiErrorResponseExamples(
            value = {UNAUTHORIZED_ACCESS_REQUEST, UNAUTHENTICATED_USER_REQUEST, USER_NOT_FOUND,
                    NICKNAME_DUPLICATION, DESERIALIZATION_FAILURE, VALIDATION_FAILURE},
            name = {"권한 없음", "인증되지 않은 사용자", "사용자 없음", "닉네임 중복", "역직렬화 실패", "검증 실패"},
            description = {"권한이 없는 요청입니다.", "인증이 필요합니다.", "존재하지 않는 사용자입니다.",
                    "중복된 닉네임입니다.", "역직렬화에 실패하였습니다.", "검증에 실패하였습니다."}
    )
    ApiResponse<String> updateUserDetails(@PathVariable("userId") Long userId,
                                          @RequestBody @Valid UserDetailsUpdateRequest userDetailsUpdateRequest,
                                          @Parameter(hidden = true) AuthenticatedUserInfo authenticatedUserInfo);

    @Operation(
            summary = "사용자 비밀번호 업데이트",
            description = "사용자의 비밀번호를 업데이트하는 API입니다. accessToken이 필요합니다.",
            tags = {"External API"},
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "사용자의 고유한 ID",
                            required = true,
                            example = "1",
                            in = ParameterIn.PATH, // PathVariable임을 명시
                            schema = @Schema(type = "integer", format = "int64")
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "사용자 비밀번호 업데이트 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PasswordUpdateRequest.class)
                    )
            )
    )
    @ApiSuccessResponseExample(responseClass = String.class, data = false, status = HttpStatus.OK)
    @ApiErrorResponseExamples(
            value = {UNAUTHORIZED_ACCESS_REQUEST, UNAUTHENTICATED_USER_REQUEST, USER_NOT_FOUND, UNCHANGED_NEW_PASSWORD,
                    WRONG_PREVIOUS_PASSWORD, DESERIALIZATION_FAILURE, VALIDATION_FAILURE},
            name = {"권한 없음", "인증되지 않은 사용자", "사용자 없음", "동일한 비밀번호",
                    "잘못된 기존 비밀번호", "닉네임 중복", "역직렬화 실패", "검증 실패"},
            description = {"권한이 없는 요청입니다.", "인증이 필요합니다.", "존재하지 않는 사용자입니다.", "새 비밀번호와 기존 비밀번호가 동일합니다.",
                    "기존 비밀번호가 틀렸습니다.", "역직렬화에 실패하였습니다.", "검증에 실패하였습니다."}
    )
    ApiResponse<String> updateUserPassword(@PathVariable("userId") Long userId,
                                           @RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest,
                                           @Parameter(hidden = true) AuthenticatedUserInfo authenticatedUserInfo);

    @Operation(
            summary = "사용자 여행 MBTI 업데이트",
            description = "사용자의 여행 MBTI를 업데이트하는 API입니다. accessToken이 필요합니다.",
            tags = {"External API"},
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "사용자의 고유한 ID",
                            required = true,
                            example = "1",
                            in = ParameterIn.PATH, // PathVariable임을 명시
                            schema = @Schema(type = "integer", format = "int64")
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "사용자 여행 MBTI 업데이트 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TripMbtiUpdateRequest.class)
                    )
            )
    )
    @ApiSuccessResponseExample(responseClass = String.class, data = false, status = HttpStatus.OK)
    @ApiErrorResponseExamples(
            value = {UNAUTHORIZED_ACCESS_REQUEST, UNAUTHENTICATED_USER_REQUEST, USER_NOT_FOUND,
                    INVALID_MBTI_REQUEST, DESERIALIZATION_FAILURE, VALIDATION_FAILURE},
            name = {"권한 없음", "인증되지 않은 사용자", "사용자 없음", "잘못된 MBTI", "역직렬화 실패", "검증 실패"},
            description = {"권한이 없는 요청입니다.", "인증이 필요합니다.", "존재하지 않는 사용자입니다.",
                    "존재하지 않는 MBTI입니다.", "역직렬화에 실패하였습니다.", "검증에 실패하였습니다."}
    )
    ApiResponse<String> updateUserMbti(@PathVariable("userId") Long userId,
                                       @RequestBody @Valid TripMbtiUpdateRequest tripMbtiUpdateRequest,
                                       @Parameter(hidden = true) AuthenticatedUserInfo authenticatedUserInfo);

    @Operation(
            summary = "블로그의 사용자 정보 리스트 조회",
            description = "블로그 서비스에서 다수의 사용자 id 값으로 사용자 정보들을 한번에 조회하는 API입니다.",
            tags = {"Internal API"},
            parameters = {
                    @Parameter(
                            name = "userIds",
                            description = "사용자의 고유한 ID 리스트",
                            required = true,
                            in = ParameterIn.QUERY,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            type = "integer",
                                            format = "int64"
                                    )
                            ),
                            style = ParameterStyle.FORM,
                            explode = Explode.TRUE,
                            example = "userIds=1&userIds=2&userIds=3"
                    )
            }
    )
    @ApiSuccessResponseExample(responseClass = List.class, data = true, status = HttpStatus.OK, genericType = UserInfoResponse.class)
    @ApiErrorResponseExamples(
            value = {DESERIALIZATION_FAILURE},
            name = {"역직렬화 실패"},
            description = {"역직렬화에 실패했습니다."}
    )
    ApiResponse<List<UserInfoResponse>> getUserList(@RequestParam("userIds") Set<Long> userIds);

    @Operation(
            summary = "S3 presigned url 생성",
            description = " 이미지 업로드가 가능한 S3 presigned url을 생성하는 API입니다.\n accessToken이 필요합니다.",
            tags = {"External API"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "S3 presigned url 생성 요청 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = S3PresignedUrlRequest.class)
                    )
            )
    )
    @ApiSuccessResponseExample(responseClass = S3PresignedUrlResponse.class, data = true, status = HttpStatus.OK)
    @ApiErrorResponseExamples(
            value = {GENERATING_PRESIGEND_URL_FAILURE, UNAUTHENTICATED_USER_REQUEST,
                    DESERIALIZATION_FAILURE, VALIDATION_FAILURE},
            name = {"URL 생성 실패", "인증되지 않은 사용자", "역직렬화 실패", "검증 실패"},
            description = {"presigned url 생성 중 문제가 발생했습니다.", "인증이 필요합니다.",
                    "역직렬화에 실페하였습니다.", "검증에 실패하였습니다."}
    )
    ApiResponse<S3PresignedUrlResponse> getPresignedUrl(
            @RequestBody @Valid S3PresignedUrlRequest s3PresignedUrlRequest,
            AuthenticatedUserInfo authenticatedUserInfo);

    @Operation(
            summary = "사용자 프로필 이미지 업데이트",
            description = " 사용자의 프로필 이미지를 업데이트하는 API입니다.\n accessToken이 필요합니다.",
            tags = {"External API"},
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "사용자의 고유한 ID",
                            required = true,
                            example = "1",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "사용자 프로필 이미지 업데이트 요청 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfileImageUrlUpdateRequest.class)
                    )
            )
    )
    @ApiSuccessResponseExample(responseClass = String.class, data = false, status = HttpStatus.OK)
    @ApiErrorResponseExamples(
            value = {UNAUTHORIZED_ACCESS_REQUEST, UNAUTHENTICATED_USER_REQUEST, USER_NOT_FOUND,
                    INVALID_PROFILE_IMAGE_URL, DESERIALIZATION_FAILURE, VALIDATION_FAILURE},
            name = {"권한 없음", "인증되지 않은 사용자", "사용자 없음", "잘못된 url 경로", "역직렬화 실패", "검증 실패"},
            description = {"권한이 없는 요청입니다.", "인증이 필요합니다.", "존재하지 않는 사용자입니다.",
                    "유효하지 않은 url입니.", "역직렬화에 실패하였습니다.", "검증에 실패하였습니다."}
    )
    ApiResponse<String> updateProfileImage(
            @PathVariable("userId") Long userId,
            @RequestBody @Valid ProfileImageUrlUpdateRequest profileImageUrlUpdateRequest,
            AuthenticatedUserInfo authenticatedUserInfo);


    @Operation(
            summary = "사용자 회원탈퇴",
            description = " 사용자가 회원탈퇴를 요청하는 API입니다.\n accessToken이 필요합니다.",
            tags = {"External API"}
    )
    @ApiSuccessResponseExample(responseClass = String.class, data = false, status = HttpStatus.OK)
    @ApiErrorResponseExamples(
            value = {UNAUTHENTICATED_USER_REQUEST},
            name = {"인증되지 않은 사용자"},
            description = {"인증이 필요합니다."}
    )
    ApiResponse<String> deleteAccount(AuthenticatedUserInfo authenticatedUserInfo);

    @Operation(
            summary = "사용자 비밀번호 초기화",
            description = "이메일을 기억하고, SMS 인증을 완료한 사용자가 비밀번호 초기화를 요청하는 API입니다.",
            tags = {"External API"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "비밀번호 초기화 요청 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PasswordResetRequest.class)
                    )
            )
    )
    @ApiSuccessResponseExample(responseClass = String.class, data = false, status = HttpStatus.ACCEPTED)
    @ApiErrorResponseExamples(
            value = {USER_NOT_FOUND, UNAUTHORIZED_ACCESS_REQUEST, INVALID_SMS_CONFIRMATION_TOKEN,
                    DESERIALIZATION_FAILURE, VALIDATION_FAILURE},
            name = {"사용자 없음", "권한 없음", "유효하지 않은 SMS 인증 토큰", "역직렬화 실패", "검증 실패"},
            description = {"존재하지 않는 사용자입니다.", "권한이 없습니다. 소셜 로그인 사용자는 비밀번호 변경이 불가합니다.",
                    "유효하지 않은 SMS 인증 토큰입니다.", "역직렬화에 실패하였습니다.", "검증에 실패하였습니다."}
    )
    ApiResponse<String> resetPassword(@RequestBody @Valid PasswordResetRequest passwordFindRequest);
}

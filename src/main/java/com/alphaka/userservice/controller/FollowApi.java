package com.alphaka.userservice.controller;

import static com.alphaka.userservice.exception.ErrorCode.*;

import com.alphaka.userservice.dto.response.ApiResponse;
import com.alphaka.userservice.dto.response.UserInfoResponse;
import com.alphaka.userservice.dto.response.UserInfoWithFollowStatusResponse;
import com.alphaka.userservice.swagger.annotation.ApiErrorResponseExamples;
import com.alphaka.userservice.swagger.annotation.ApiSuccessResponseExample;
import com.alphaka.userservice.util.AuthenticatedUserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "유저 API", description = "유저 관련 API")
public interface FollowApi {

    @Operation(
            summary = "사용자 팔로우 요청",
            description = "다른 사용자를 팔로우하는 API 입니다. accessToken이 필요합니다.",
            tags = {"External API"},
            parameters = {
                    @Parameter(
                            name = "targetUserId",
                            description = "팔로우할 사용자의 고유한 ID",
                            required = true,
                            example = "1",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            }
    )
    @ApiSuccessResponseExample(responseClass = String.class, data = false, status = HttpStatus.OK)
    @ApiErrorResponseExamples(
            value = {UNAUTHENTICATED_USER_REQUEST, USER_NOT_FOUND, INVALID_FOLLOW_REQUEST},
            name = {"인증되지 않은 사용자", "사용자 없음", "유효하지 않은 팔로우"},
            description = {"인증이 필요합니다", "존재하지 않는 사용자입니다.", "유효하지 않은 팔로우 요청입니다."}
    )
    ApiResponse<String> follow(@PathVariable("targetUserId") Long targetUserId,
                               @Parameter(hidden = true) AuthenticatedUserInfo authenticatedUserInfo);

    @Operation(
            summary = "사용자 언팔로우 요청",
            description = "다른 사용자를 언팔로우하는 API 입니다. accessToken이 필요합니다.",
            tags = {"External API"},
            parameters = {
                    @Parameter(
                            name = "targetUserId",
                            description = "언팔로우할 사용자의 고유한 ID",
                            required = true,
                            example = "1",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            }
    )
    @ApiSuccessResponseExample(responseClass = String.class, data = false, status = HttpStatus.OK)
    @ApiErrorResponseExamples(
            value = {UNAUTHENTICATED_USER_REQUEST, USER_NOT_FOUND, INVALID_FOLLOW_REQUEST},
            name = {"인증되지 않은 사용자", "사용자 없음", "유효하지 않은 팔로우"},
            description = {"인증이 필요합니다", "존재하지 않는 사용자입니다.", "유효하지 않은 안팔로우 요청입니다."}
    )
    ApiResponse<String> unfollow(@PathVariable("targetUserId") Long targetUserId,
                                 @Parameter(hidden = true) AuthenticatedUserInfo authenticatedUserInfo);

    @Operation(
            summary = "사용자 팔로잉 목록 조회 요청",
            description = "해당 id를 가진 사용자의 팔로잉 목록을 조회하는 API 입니다. accessToken을 담아 인증 필터를 거치게 할 경우 자신과의 팔로우 여부를 나타내는 followStatus가 함께 반환합니다. 인증 필터를 거치지 않는다면 기본적으로 followStatus값이  false가 반환됩니다.",
            tags = {"External API"},
            parameters = {
                    @Parameter(
                            name = "targetUserId",
                            description = "사용자의 고유한 ID",
                            required = true,
                            example = "1",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            }
    )
    @ApiSuccessResponseExample(responseClass = List.class, data = true, status = HttpStatus.OK, genericType = UserInfoWithFollowStatusResponse.class)
    @ApiErrorResponseExamples(
            value = {USER_NOT_FOUND},
            name = {"사용자 없음"},
            description = {"존재하지 않는 사용자입니다."}
    )
    ApiResponse<List<UserInfoWithFollowStatusResponse>> following(
            @PathVariable("targetUserId") Long targetUserId, HttpServletRequest request);

    @Operation(
            summary = "사용자 팔로워 목록 조회 요청",
            description = "해당 id를 가진 사용자의 팔로워 목록을 조회하는 API 입니다. accessToken을 담아 인증 필터를 거치게 할 경우 자신과의 팔로우 여부도 함께 반환합니다.  인증 필터를 거치지 않는다면 기본적으로 followStatus값이  false가 반환됩니다.",
            tags = {"External API"},
            parameters = {
                    @Parameter(
                            name = "targetUserId",
                            description = "사용자의 고유한 ID",
                            required = true,
                            example = "1",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            }
    )
    @ApiSuccessResponseExample(responseClass = List.class, data = true, status = HttpStatus.OK, genericType = UserInfoWithFollowStatusResponse.class)
    @ApiErrorResponseExamples(
            value = {USER_NOT_FOUND},
            name = {"사용자 없음"},
            description = {"존재하지 않는 사용자입니다."}
    )
    ApiResponse<List<UserInfoWithFollowStatusResponse>> follower(
            @PathVariable("targetUserId") Long targetUserId, HttpServletRequest request);

    @Operation(
            summary = "사용자 팔로우 여부 조회 요청",
            description = "쿼리 파라미터로 해당 사용자를 팔로우 하고 있는지 조회하는 API 입니다. accessToken이 필요합니다.",
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
    @ApiSuccessResponseExample(responseClass = Boolean.class, data = true, status = HttpStatus.OK)
    @ApiErrorResponseExamples(
            value = {USER_NOT_FOUND, UNAUTHENTICATED_USER_REQUEST},
            name = {"사용자 없음", "인증되지 않은 사용자"},
            description = {"존재하지 않는 사용자입니다.", "인증이 필요합니다."}
    )
    ApiResponse<Boolean> followStatus(@RequestParam("userId") Long userId,
                                      @Parameter(hidden = true) AuthenticatedUserInfo authenticatedUserInfo);
}

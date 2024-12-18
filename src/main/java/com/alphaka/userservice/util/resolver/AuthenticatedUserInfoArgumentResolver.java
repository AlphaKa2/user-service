package com.alphaka.userservice.util.resolver;

import static com.alphaka.userservice.util.UserInfoHeader.*;

import com.alphaka.userservice.entity.Role;
import com.alphaka.userservice.exception.custom.UnatuhenticatedUserRequestException;
import com.alphaka.userservice.util.AuthenticatedUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
public class AuthenticatedUserInfoArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(AuthenticatedUserInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        log.info("인증된 유저 정보 추출");
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        String id = request.getHeader(AUTHENTICATED_USER_ID_HEADER.getName());
        if (id == null) {
            log.error("인증된 사용자의 요청이 아닙니다.");
            throw new UnatuhenticatedUserRequestException();
        }
        Long userId = Long.valueOf(id);
        String profileImage = request.getHeader(AUTHENTICATED_USER_PROFILE_HEADER.getName());
        String nickname = request.getHeader(AUTHENTICATED_USER_NICKNAME_HEADER.getName());
        Role role = Role.getRole(request.getHeader(AUTHENTICATED_USER_ROLE_HEADER.getName()));

        return new AuthenticatedUserInfo(userId, profileImage, nickname, role);
    }
}

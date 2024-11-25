package com.alphaka.userservice.service;

import com.alphaka.userservice.dto.request.OAuth2SignInRequest;
import com.alphaka.userservice.dto.request.PasswordUpdateRequest;
import com.alphaka.userservice.dto.request.TripMbtiUpdateRequest;
import com.alphaka.userservice.dto.request.UserDetailsUpdateRequest;
import com.alphaka.userservice.dto.request.UserSignInRequest;
import com.alphaka.userservice.dto.request.UserSignUpRequest;
import com.alphaka.userservice.dto.response.UserInfoResponse;
import com.alphaka.userservice.entity.SocialType;
import com.alphaka.userservice.entity.TripMBTI;
import com.alphaka.userservice.entity.User;
import com.alphaka.userservice.exception.custom.EmailDuplicationException;
import com.alphaka.userservice.exception.custom.InvalidMbtiRequestException;
import com.alphaka.userservice.exception.custom.NicknameDuplicationException;
import com.alphaka.userservice.exception.custom.UnauthorizedAccessReqeust;
import com.alphaka.userservice.exception.custom.UnchangedNewPasswordException;
import com.alphaka.userservice.exception.custom.UserNotFoundException;
import com.alphaka.userservice.exception.custom.WrongPreviousPasswordException;
import com.alphaka.userservice.kafka.service.UserSignupProducerService;
import com.alphaka.userservice.repository.UserRepository;
import com.alphaka.userservice.util.AuthenticatedUserInfo;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSignupProducerService userSignupProducerService;

    public User getUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 사용자입니다. {}", userId);
                    return new UserNotFoundException();
                });
    }

    public User getUserByNicknameOrThrow(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 사용자입니다. {}", nickname);
                    return new UserNotFoundException();
                });
    }

    public User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 사용자입니다. {}", email);
                    return new UserNotFoundException();
                });
    }

    public void checkNicknameDuplication(String nickname) {
        if (userRepository.findByNickname(nickname).isPresent()) {
            log.error("중복되는 닉네임입니다. {}", nickname);
            throw new NicknameDuplicationException();
        }
    }

    public void checkEmailDuplication(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            log.error("중복되는 이메일입니다. {}", email);
            throw new EmailDuplicationException();
        }
    }

    @Transactional
    public void join(UserSignUpRequest userSignUpRequest) {

        // 이메일 중복 검사
        checkEmailDuplication(userSignUpRequest.getEmail());

        // 닉네임 중복 검사
        checkNicknameDuplication(userSignUpRequest.getNickname());

        userSignUpRequest.setPassword(passwordEncoder.encode(userSignUpRequest.getPassword()));
        User savedUser = userRepository.save(userSignUpRequest.toEntity());

        //회원 생성 이벤트 전송
        log.info("회원 {} 생성 이벤트 메시지 전송", savedUser.getId());
        userSignupProducerService.sendMessage(savedUser.getId());
    }

    @Transactional
    public User oauth2SignIn(OAuth2SignInRequest oAuth2SignInRequest) {
        String email = oAuth2SignInRequest.getEmail();
        String nickname = oAuth2SignInRequest.getNickname();
        SocialType socialType = oAuth2SignInRequest.getSocialType();

        //자체 회원 혹은 다른 소셜 로그인을 통해 이미 같은 이메일로 가입한 경우
        verifyEmailUniquenessForSocialLogin(email, socialType);

        // 닉네임이 중복된다면 랜덤한 숫자 추가하여 새로운 닉네임 생성
        String validNickname = generateUniqueNickname(nickname);
        oAuth2SignInRequest.setNickname(validNickname);

        //유일한 이메일인 경우 소셜 로그인 성공, 만약 DB에 존재하지 않다면 가입
        User user = getOAuth2UserOrCreate(oAuth2SignInRequest);

        user.updateLastLogin();

        return user;
    }


    public User findUserByIdOrNickname(Long id, String nickname) {
        User user = null;
        if (id != null) {
            user = getUserByIdOrThrow(id);
        } else if (nickname != null) {
            user = getUserByNicknameOrThrow(nickname);
        } else {
            log.error("파라미터에 id 혹은 닉네임이 반드시 존재해야합니다.");
            throw new UserNotFoundException();
        }

        return user;
    }

    public User signIn(UserSignInRequest userSignInRequest) {
        String email = userSignInRequest.getEmail();

        return getUserByEmailOrThrow(email);
    }


    @Transactional
    public void disableUser(String email) {
        User user = getUserByEmailOrThrow(email);

        user.disable();
        userRepository.save(user);
    }

    @Transactional
    public void updateUserDetails(Long userId, UserDetailsUpdateRequest userDetailsUpdateRequest,
                                  AuthenticatedUserInfo authenticatedUserInfo) {

        verifyUserAuthorization(userId, authenticatedUserInfo);
        User user = getUserByIdOrThrow(userId);

        String newNickname = userDetailsUpdateRequest.getNickname();

        //새로운 닉네임이라면 중복 체크
        if (!newNickname.equals(user.getNickname())) {
            log.info("닉네임 변경 시도 {}", newNickname);
            checkNicknameDuplication(newNickname);
        }

        user.updateNickname(newNickname);
        user.updateProfileDescription(userDetailsUpdateRequest.getProfileDescription());

    }


    @Transactional
    public void updatePassword(Long userId, PasswordUpdateRequest passwordUpdateRequest,
                               AuthenticatedUserInfo authenticatedUserInfo) {

        verifyUserAuthorization(userId, authenticatedUserInfo);

        String currentPassword = passwordUpdateRequest.getPreviousPassword();
        String newPassword = passwordUpdateRequest.getNewPassword();

        verifyPasswordChange(currentPassword, newPassword);

        User user = getUserByIdOrThrow(userId);

        // 소셜 로그인 사용자는 비밀번호 변경 불가
        verifyNonSocialUser(user);

        // 기존 비밀번호가 올바른지 검증
        verifyCurrentPassword(currentPassword, user);

        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void updateMbti(Long userId, TripMbtiUpdateRequest tripMbtiUpdateRequest,
                           AuthenticatedUserInfo authenticatedUserInfo) {

        verifyUserAuthorization(userId, authenticatedUserInfo);

        User user = getUserByIdOrThrow(userId);

        TripMBTI newMbti = getMbtiOrThrow(tripMbtiUpdateRequest);

        user.updateMbti(newMbti);
    }


    // 다중 사용자 조회
    public List<UserInfoResponse> findUsersByIds(Set<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        return users.stream()
                .map(UserInfoResponse::fromUser)
                .collect(Collectors.toList());
    }


    private String generateUniqueNickname(String nickname) {
        String validNickname = nickname;
        while (userRepository.findByNickname(validNickname).isPresent()) {
            validNickname = nickname + new Random().nextInt(1000);
            log.warn("닉네임 중복으로 인한 새 닉네임 자동 생성, 기존 닉네임:{} 새 닉네임: {}", nickname, validNickname);
        }
        return validNickname;
    }

    private User getOAuth2UserOrCreate(OAuth2SignInRequest oAuth2SignInRequest) {
        String email = oAuth2SignInRequest.getEmail();
        SocialType socialType = oAuth2SignInRequest.getSocialType();

        User user = userRepository.findByEmailAndSocialType(email, socialType)
                .orElseGet(() -> {
                    log.info("DB에 존재하지 않는 사용자입니다. 새로 가입합니다. 이메일: {}, 소셜 타입: {}", email, socialType);
                    return userRepository.save(oAuth2SignInRequest.toEntity());
                });

        user.updateLastLogin();
        return user;
    }

    private TripMBTI getMbtiOrThrow(TripMbtiUpdateRequest tripMbtiUpdateRequest) {
        TripMBTI newMbti;
        try {
            newMbti = TripMBTI.valueOf(tripMbtiUpdateRequest.getMbti());
        } catch (Exception e) {
            log.error("유효하지 않은 여행 MBTI입니다.");
            throw new InvalidMbtiRequestException();
        }
        return newMbti;
    }

    private void verifyEmailUniquenessForSocialLogin(String email, SocialType socialType) {
        if (userRepository.findByEmailAndSocialTypeNot(email, socialType).isPresent()) {
            log.error("자체 회원 혹은 다른 소셜 로그인으로 이미 가입된 이메일입니다. {}", email);
            throw new EmailDuplicationException();
        }
    }

    private void verifyUserAuthorization(Long userId, AuthenticatedUserInfo authenticatedUserInfo) {
        if (!userId.equals(authenticatedUserInfo.getId())) {
            log.error("권한이 없습니다.");
            throw new UnauthorizedAccessReqeust();
        }
    }

    private void verifyPasswordChange(String previousPassword, String newPassword) {
        if (previousPassword.equals(newPassword)) {
            log.error("새 비밀번호가 기존 비밀번호와 동일합니다.");
            throw new UnchangedNewPasswordException();
        }
    }

    private void verifyNonSocialUser(User user) {
        if (user.getSocialType() != SocialType.EMAIL) {
            log.error("소셜 로그인 유저가 비밀번호 변경을 시도합니다.");
            throw new UnauthorizedAccessReqeust();
        }
    }

    private void verifyCurrentPassword(String previousPassword, User user) {
        if (!passwordEncoder.matches(previousPassword, user.getPassword())) {
            log.error("기존 비밀번호가 올바르지 않습니다.");
            throw new WrongPreviousPasswordException();
        }
    }

}

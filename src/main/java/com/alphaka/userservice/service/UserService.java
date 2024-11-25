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
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
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

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public void join(UserSignUpRequest userSignUpRequest) {

        // 이메일 중복 검사
        if (userRepository.findByEmail(userSignUpRequest.getEmail()).isPresent()) {
            log.error("중복되는 이메일입니다. {}", userSignUpRequest.getEmail());
            throw new EmailDuplicationException();
        }

        // 닉네임 중복 검사
        if (userRepository.findByNickname(userSignUpRequest.getNickname()).isPresent()) {
            log.error("중복되는 닉네임입니다. {}", userSignUpRequest.getNickname());
            throw new NicknameDuplicationException();
        }

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
        if (userRepository.findByEmailAndSocialTypeNot(email, socialType).isPresent()) {
            log.error("자체 회원 혹은 다른 소셜 로그인으로 이미 가입된 이메일입니다. {}", email);
            throw new EmailDuplicationException();
        }

        // 닉네임 중복 검사, 중복된다면 랜덤한 숫자 추가.
        String validNickname = nickname;
        while (userRepository.findByNickname(validNickname).isPresent()) {
            validNickname = nickname + new Random().nextInt(100000000);
            log.warn("닉네임 중복으로 인한 새 닉네임 자동 생성, 기존 닉네임:{} 새 닉네임: {}", nickname, validNickname);
        }
        oAuth2SignInRequest.setNickname(validNickname);

        //유일한 이메일인 경우 소셜 로그인 성공, 만약 DB에 존재하지 않다면 가입
        User user = userRepository.findByEmailAndSocialType(email, socialType)
                .orElseGet(() -> userRepository.save(oAuth2SignInRequest.toEntity()));

        user.updateLastLogin();

        return user;
    }


    public User findUserByIdOrNickname(Long id, String nickname) {
        User user = null;
        if (id != null) {
            log.info("id로 조회 {}", id);
            user = findUserById(id);
        } else if (nickname != null) {
            log.info("닉네임으로 조회 {}", nickname);
            user = findUserByNickname(nickname);
        } else {
            log.error("id 혹은 닉네임이 반드시 존재해야합니다. ");
            throw new UserNotFoundException();
        }

        return user;
    }

    public User signIn(UserSignInRequest userSignInRequest) {
        String email = userSignInRequest.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        return user;
    }

    public User findUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(UserNotFoundException::new);
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
    public void disableUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        user.disable();
        userRepository.save(user);
    }

    @Transactional
    public void updateUserDetails(Long userId, UserDetailsUpdateRequest userDetailsUpdateRequest,
                                  AuthenticatedUserInfo authenticatedUserInfo) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        String newNickname = userDetailsUpdateRequest.getNickname();

        //새로운 닉네임이라면 중복 체크
        if (!newNickname.equals(user.getNickname())) {
            log.info("닉네임 변경 시도 {}", newNickname);
            Optional<User> maybeUserWithNewNickname = userRepository.findByNickname(newNickname);

            if (maybeUserWithNewNickname.isPresent()) {
                log.error("중복되는 닉네임입니다. {}", newNickname);
                throw new NicknameDuplicationException();
            }
        }

        user.updateNickname(newNickname);
        user.updateProfileDescription(userDetailsUpdateRequest.getProfileDescription());

    }

    @Transactional
    public void updatePassword(Long userId, PasswordUpdateRequest passwordUpdateRequest,
                               AuthenticatedUserInfo authenticatedUserInfo) {

        if (!userId.equals(authenticatedUserInfo.getId())) {
            log.error("다른 사용자의 정보를 수정하려 합니다.");
            throw new UnauthorizedAccessReqeust();
        }

        String previousPassword = passwordUpdateRequest.getPreviousPassword();
        String newPassword = passwordUpdateRequest.getNewPassword();

        if (previousPassword.equals(newPassword)) {
            log.error("새 비밀번호가 기존 비밀번호와 동일합니다.");
            throw new UnchangedNewPasswordException();
        }

        User user = userRepository.findById(authenticatedUserInfo.getId())
                .orElseThrow(UserNotFoundException::new);

        // 소셜 로그인 사용자라면
        if (user.getSocialType() != SocialType.EMAIL) {
            log.error("소셜 로그인 유저가 비밀번호 변경을 시도합니다.");
            throw new UnauthorizedAccessReqeust();
        }

        if (!passwordEncoder.matches(previousPassword, user.getPassword())) {
            log.error("기존 비밀번호가 올바르지 않습니다.");
            throw new WrongPreviousPasswordException();
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void updateMbti(Long userId, TripMbtiUpdateRequest tripMbtiUpdateRequest,
                           AuthenticatedUserInfo authenticatedUserInfo) {

        if (!userId.equals(authenticatedUserInfo.getId())) {
            log.error("다른 사용자의 정보를 수정하려 합니다.");
            throw new UnauthorizedAccessReqeust();
        }

        User user = userRepository.findById(authenticatedUserInfo.getId())
                .orElseThrow(UserNotFoundException::new);

        TripMBTI newMbti;
        try {
            newMbti = TripMBTI.valueOf(tripMbtiUpdateRequest.getMbti());
        } catch (Exception e) {
            log.error("유효하지 않은 여행 MBTI입니다.");
            throw new InvalidMbtiRequestException();
        }

        user.updateMbti(newMbti);
    }

    // 다중 사용자 조회
    public List<UserInfoResponse> findUsersByIds(Set<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        return users.stream()
                .map(UserInfoResponse::fromUser)
                .collect(Collectors.toList());
    }
}

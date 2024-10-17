package com.alphaka.userservice.service;

import com.alphaka.userservice.dto.request.OAuth2SignInRequest;
import com.alphaka.userservice.dto.request.PasswordUpdateRequest;
import com.alphaka.userservice.dto.request.TripMbtiUpdateRequest;
import com.alphaka.userservice.dto.request.UserDetailsUpdateRequest;
import com.alphaka.userservice.dto.request.UserSignInRequest;
import com.alphaka.userservice.dto.request.UserSignUpRequest;
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
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new EmailDuplicationException();
        }

        // 닉네임 중복 검사
        if (userRepository.findByNickname(userSignUpRequest.getNickname()).isPresent()) {
            throw new NicknameDuplicationException();
        }

        userSignUpRequest.setPassword(passwordEncoder.encode(userSignUpRequest.getPassword()));
        User savedUser = userRepository.save(userSignUpRequest.toEntity());

        //회원 생성 이벤트 전송
        userSignupProducerService.sendMessage(savedUser.getId());
    }

    @Transactional
    public User oauth2SignIn(OAuth2SignInRequest oAuth2SignInRequest) {
        String email = oAuth2SignInRequest.getEmail();
        String nickname = oAuth2SignInRequest.getNickname();
        SocialType socialType = oAuth2SignInRequest.getSocialType();

        //자체 회원 혹은 다른 소셜 로그인을 통해 이미 같은 이메일로 가입한 경우
        if (userRepository.findByEmailAndSocialTypeNot(email, socialType).isPresent()) {
            throw new EmailDuplicationException();
        }

        // 닉네임 중복 검사, 중복된다면 랜덤한 숫자 추가.
        String validNickname = nickname;
        while (!userRepository.findByNickname(validNickname).isPresent()) {
            validNickname = nickname + new Random().nextInt(100000000);
        }
        oAuth2SignInRequest.setNickname(validNickname);

        //유일한 이메일인 경우 소셜 로그인 성공, 만약 DB에 존재하지 않다면 가입
        User user = userRepository.findByEmailAndSocialType(email, socialType)
                .orElseGet(() -> userRepository.save(oAuth2SignInRequest.toEntity()));

        user.updateLastLogin();

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
                .orElseThrow(NicknameDuplicationException::new);
    }

    @Transactional
    public void disableUser(String email) {
        User user = userRepository.findByEmail(email).get();
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
            Optional<User> maybeUserWithNewNickname = userRepository.findByNickname(newNickname);

            if (maybeUserWithNewNickname.isPresent()) {
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
            throw new UnauthorizedAccessReqeust();
        }

        String previousPassword = passwordUpdateRequest.getPreviousPassword();
        String newPassword = passwordUpdateRequest.getNewPassword();

        if (previousPassword.equals(newPassword)) {
            throw new UnchangedNewPasswordException();
        }

        User user = userRepository.findById(authenticatedUserInfo.getId()).get();

        // 소셜 로그인 사용자라면
        if (user.getSocialType() != SocialType.EMAIL) {
            throw new UnauthorizedAccessReqeust();
        }

        if (!passwordEncoder.matches(previousPassword, user.getPassword())) {
            throw new WrongPreviousPasswordException();
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void updateMbti(Long userId, TripMbtiUpdateRequest tripMbtiUpdateRequest,
                           AuthenticatedUserInfo authenticatedUserInfo) {

        if (!userId.equals(authenticatedUserInfo.getId())) {
            throw new UnauthorizedAccessReqeust();
        }

        User user = userRepository.findById(authenticatedUserInfo.getId()).get();

        TripMBTI newMbti;
        try {
            newMbti = TripMBTI.valueOf(tripMbtiUpdateRequest.getMbti());
        } catch (Exception e) {
            throw new InvalidMbtiRequestException();
        }

        user.updateMbti(newMbti);
    }
}

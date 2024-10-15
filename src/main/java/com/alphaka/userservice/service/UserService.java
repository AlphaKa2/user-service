package com.alphaka.userservice.service;

import com.alphaka.userservice.dto.request.OAuth2SignInRequest;
import com.alphaka.userservice.dto.request.UserDetailsUpdateRequest;
import com.alphaka.userservice.dto.request.UserSignInRequest;
import com.alphaka.userservice.dto.request.UserSignUpRequest;
import com.alphaka.userservice.dto.response.UserSignInResponse;
import com.alphaka.userservice.entity.SocialType;
import com.alphaka.userservice.entity.User;
import com.alphaka.userservice.exception.custom.NicknameDuplicationException;
import com.alphaka.userservice.exception.custom.UserNotFoundException;
import com.alphaka.userservice.kafka.service.UserSignupProducerService;
import com.alphaka.userservice.repository.UserRepository;
import com.alphaka.userservice.util.AuthenticatedUserInfo;
import java.util.Optional;
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

    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    public Optional<User> join(UserSignUpRequest userSignUpRequest) {
        if (userRepository.findByEmail(userSignUpRequest.getEmail()).isPresent()) {
            return Optional.empty();
        }

        userSignUpRequest.setPassword(passwordEncoder.encode(userSignUpRequest.getPassword()));
        User savedUser = userRepository.save(userSignUpRequest.toEntity());

        //회원 생성 이벤트 전송
        userSignupProducerService.sendMessage(savedUser.getId());
        return Optional.of(savedUser);
    }

    @Transactional
    public Optional<UserSignInResponse> oauth2SignIn(OAuth2SignInRequest oAuth2SignInRequest) {
        String email = oAuth2SignInRequest.getEmail();
        SocialType socialType = oAuth2SignInRequest.getSocialType();

        //자체 회원 혹은 다른 소셜 로그인을 통해 이미 같은 이메일로 가입한 경우
        if (userRepository.findByEmailAndSocialTypeNot(email, socialType).isPresent()) {
            return Optional.empty();
        }

        //유일한 이메일인 경우 소셜 로그인 성공, 만약 DB에 존재하지 않다면 가입
        User user = userRepository.findByEmailAndSocialType(email, socialType)
                .orElseGet(() -> userRepository.save(oAuth2SignInRequest.toEntity()));

        user.updateLastLogin();

        return Optional.of(UserSignInResponse.userSignInResponseFromUser(user));
    }

    public Optional<UserSignInResponse> signIn(UserSignInRequest userSignInRequest) {
        String email = userSignInRequest.getEmail();

        Optional<User> maybeUser = userRepository.findByEmail(email);
        return maybeUser.map(UserSignInResponse::userSignInResponseWithPasswordFromUser);

    }

    public Optional<User> findUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
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
        Optional<User> maybeUser = userRepository.findById(userId);

        if (maybeUser.isEmpty() || !userId.equals(authenticatedUserInfo.getId())) {
            throw new UserNotFoundException();
        }

        User user = maybeUser.get();
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

}

package com.alphaka.userservice.service;

import com.alphaka.userservice.dto.request.OAuth2SignInRequest;
import com.alphaka.userservice.dto.request.UserSignInRequest;
import com.alphaka.userservice.dto.request.UserSignUpRequest;
import com.alphaka.userservice.dto.response.UserSignInResponse;
import com.alphaka.userservice.entity.SocialType;
import com.alphaka.userservice.entity.User;
import com.alphaka.userservice.repository.UserRepository;
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

    public Optional<UserSignInResponse> findUserById(Long userId) {
        Optional<User> maybeUser = userRepository.findById(userId);
        return maybeUser.map(UserSignInResponse::userSignInResponse);
    }

    @Transactional
    public Optional<User> join(UserSignUpRequest userSignUpRequest) {
        if (userRepository.findByEmail(userSignUpRequest.getEmail()).isPresent()) {
            return Optional.empty();
        }

        userSignUpRequest.setPassword(passwordEncoder.encode(userSignUpRequest.getPassword()));
        return Optional.of(userRepository.save(userSignUpRequest.toEntity()));
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

        return Optional.of(UserSignInResponse.userSignInResponse(user));
    }

    public Optional<UserSignInResponse> signIn(UserSignInRequest userSignInRequest) {
        String email = userSignInRequest.getEmail();


        Optional<User> maybeUser = userRepository.findByEmail(email);
        return maybeUser.map(UserSignInResponse::userSignInResponseWithPassword);

    }

    @Transactional
    public void disableUser(String email) {
        User user = userRepository.findByEmail(email).get();
        user.disable();

        userRepository.save(user);
    }


}

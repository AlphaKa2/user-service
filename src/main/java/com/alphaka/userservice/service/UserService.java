package com.alphaka.userservice.service;

import com.alphaka.userservice.dto.request.UserSignUpRequest;
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

    @Transactional
    public Optional<User> join(UserSignUpRequest userSignUpRequest) {
        if (userRepository.findByEmail(userSignUpRequest.getEmail()).isPresent()) {
            return Optional.empty();
        }

        userSignUpRequest.setPassword(passwordEncoder.encode(userSignUpRequest.getPassword()));
        return Optional.of(userRepository.save(userSignUpRequest.toEntity()));
    }

}

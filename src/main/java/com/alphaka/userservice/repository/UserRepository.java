package com.alphaka.userservice.repository;

import com.alphaka.userservice.entity.SocialType;
import com.alphaka.userservice.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndSocialType(String email, SocialType socialType);

    Optional<User> findByEmailAndSocialTypeNot(String email, SocialType socialType);
}

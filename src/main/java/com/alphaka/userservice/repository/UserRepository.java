package com.alphaka.userservice.repository;

import com.alphaka.userservice.entity.SocialType;
import com.alphaka.userservice.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmailWithoutDeleted(@Param(value = "email") String email);

    @Query(value = "SELECT * FROM users WHERE email = :email AND social_type = :socialType", nativeQuery = true)
    Optional<User> findByEmailAndSocialType(@Param(value = "email") String email,
                                            @Param(value = "socialType") SocialType socialType);

    @Query(value = "SELECT * FROM users WHERE email = :email AND social_type != :socialType", nativeQuery = true)
    Optional<User> findByEmailAndSocialTypeNot(@Param(value = "email") String email,
                                               @Param(value = "socialType") SocialType socialType);

    Optional<User> findByNickname(String nickname);

    @Query(value = "SELECT * FROM users WHERE nickname = :nickname", nativeQuery = true)
    Optional<User> findByNicknameWithoutDeleted(@Param(value = "nickname") String nickname);
}

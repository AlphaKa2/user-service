package com.alphaka.userservice.repository;

import com.alphaka.userservice.entity.Follow;
import com.alphaka.userservice.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerAndFollowed(User follower, User followed);

}

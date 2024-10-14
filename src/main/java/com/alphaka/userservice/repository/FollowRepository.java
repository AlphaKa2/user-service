package com.alphaka.userservice.repository;

import com.alphaka.userservice.entity.Follow;
import com.alphaka.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    void deleteByFollowerAndFollowed(User follower,User followed);
    boolean existsByFollowerAndFollowed(User follower, User followed);
}

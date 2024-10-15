package com.alphaka.userservice.repository;

import com.alphaka.userservice.dto.response.UserInfoResponse;
import com.alphaka.userservice.entity.Follow;
import com.alphaka.userservice.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerAndFollowed(User follower, User followed);


    // 해당 사용자가 팔로우하는 유저들
    @Query("select new com.alphaka.userservice.dto.response.UserInfoResponse(u.id, u.nickname, u.profileImage) "
            + "from Follow f join f.followed u where f.follower.id = :userId")
    List<UserInfoResponse> findFollowingsByUserId(@Param("userId") Long userId);


    // 해당 사용자를 팔로우하는 유저들
    @Query("select new com.alphaka.userservice.dto.response.UserInfoResponse(u.id, u.nickname, u.profileImage) "
            + "from Follow f join f.follower u where f.followed.id = :userId")
    List<UserInfoResponse> findFollowersByUserId(@Param("userId") Long userId);

}

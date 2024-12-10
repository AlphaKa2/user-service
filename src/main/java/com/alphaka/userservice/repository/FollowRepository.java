package com.alphaka.userservice.repository;

import com.alphaka.userservice.dto.response.UserInfoWithFollowStatusResponse;
import com.alphaka.userservice.entity.Follow;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query(value = "SELECT * FROM follow f WHERE f.follower_id = :followerId AND f.followed_id = :followedId", nativeQuery = true)
    Optional<Follow> findByFollowerAndFollowed(@Param("followerId") Long followerId,
                                               @Param("followedId") Long followedId);


    // 해당 사용자가 팔로우하는 유저들
    @Query("select new com.alphaka.userservice.dto.response.UserInfoWithFollowStatusResponse(u.id, u.nickname, u.profileImage, false) "
            + "from Follow f join f.followed u where f.follower.id = :userId")
    List<UserInfoWithFollowStatusResponse> findFollowingsByUserId(@Param("userId") Long userId);


    // 해당 사용자를 팔로우하는 유저들
    @Query("select new com.alphaka.userservice.dto.response.UserInfoWithFollowStatusResponse(u.id, u.nickname, u.profileImage, false) "
            + "from Follow f join f.follower u where f.followed.id = :userId")
    List<UserInfoWithFollowStatusResponse> findFollowersByUserId(@Param("userId") Long userId);

    @Query(name = "find_followings_with_follow_status", nativeQuery = true)
    List<UserInfoWithFollowStatusResponse> findFollowingsWithFollowStatusByRequestUserIdAndTargetUserId(
            @Param("requestUserId") Long requestUserId, @Param("targetUserId") Long targetUserId);

    @Query(name = "find_followers_with_follow_status", nativeQuery = true)
    List<UserInfoWithFollowStatusResponse> findFollowersWithFollowStatusByRequestUserIdAndTargetUserId(
            @Param("requestUserId") Long requestUserId, @Param("targetUserId") Long targetUserId);


}

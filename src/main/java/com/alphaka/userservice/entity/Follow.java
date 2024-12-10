package com.alphaka.userservice.entity;

import com.alphaka.userservice.dto.response.UserInfoWithFollowStatusResponse;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"follower_id", "followed_id"})
})
@NamedNativeQuery(
        name = "find_followings_with_follow_status",
        query = """
                SELECT
                        u.user_id AS userId,
                        u.nickname AS nickname,
                        u.profile_image AS profileImage,
                        CASE
                            WHEN f1.followed_id IS NOT NULL THEN TRUE
                            ELSE FALSE
                            END AS followStatus
                FROM follow f
                INNER JOIN
                    users u ON u.user_id = f.followed_id
                        AND u.deleted_at IS NULL
                LEFT JOIN
                    follow f1 ON f1.followed_id = u.user_id
                        AND f1.follower_id = :requestUserId
                WHERE
                    f.follower_id = :targetUserId
                """,
        resultSetMapping = "UserInfoWithFollowStatusMapping")
@NamedNativeQuery(
        name = "find_followers_with_follow_status",
        query = """
                SELECT
                        u.user_id AS userId,
                        u.nickname AS nickname,
                        u.profile_image AS profileImage,
                        CASE
                            WHEN f1.followed_id IS NOT NULL THEN TRUE
                            ELSE FALSE
                            END AS followStatus
                FROM follow f
                INNER JOIN
                    users u ON u.user_id = f.follower_id
                        AND u.deleted_at IS NULL
                LEFT JOIN
                    follow f1 ON f1.followed_id = u.user_id
                        AND f1.follower_id = :requestUserId
                WHERE
                    f.followed_id = :targetUserId
                """,
        resultSetMapping = "UserInfoWithFollowStatusMapping")
@SqlResultSetMapping(
        name = "UserInfoWithFollowStatusMapping",
        classes = @ConstructorResult(
                targetClass = UserInfoWithFollowStatusResponse.class,
                columns = {
                        @ColumnResult(name = "userId", type = Long.class),
                        @ColumnResult(name = "nickname", type = String.class),
                        @ColumnResult(name = "profileImage", type = String.class),
                        @ColumnResult(name = "followStatus", type = Boolean.class)
                }
        )
)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long id;

    // 팔로우하는 유저
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE) // FetchNotFound 무시
    private User follower;

    // 팔로우된 유저
    @ManyToOne
    @JoinColumn(name = "followed_id", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private User followed;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

}

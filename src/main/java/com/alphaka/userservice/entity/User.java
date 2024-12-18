package com.alphaka.userservice.entity;

import com.alphaka.userservice.config.UserConfig;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "Users")
@SQLRestriction("deleted_at IS NULL")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    // 실제 이름
    private String name;

    // 전화 번호
    private String phoneNumber;

    private LocalDate birth;

    @Column(nullable = false)
    private String profileImage;

    // 유저의 팔로잉 리스트(1:N 관계)
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Follow> following = new HashSet<>();

    // 유저의 팔로워 리스트 (1:N 관계)
    @OneToMany(mappedBy = "followed", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Follow> followers = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TripMBTI mbti = TripMBTI.NONE;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String profileDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // 로그인 타입
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialType socialType;

    // 계정 활성화, 블랙리스트 관리용
    @Builder.Default
    private boolean isActive = true;

    private LocalDateTime lastLoginAt;

    // 계정 삭제 시간, 바로 계정 정보를 삭제하지 않고, 일정기간 동안 보관 예정
    private LocalDateTime deletedAt;

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileDescription(String profileDescription) {
        this.profileDescription = profileDescription;
    }

    public void updatePassword(String encryptedPassword) {
        this.password = encryptedPassword;
    }

    public void updateMbti(TripMBTI mbti) {
        this.mbti = mbti;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImage = profileImageUrl;
    }

    // 계정 잠김
    public void disable() {
        this.isActive = false;
    }

    // 계정 소프트 딜리트
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    @PrePersist
    public void setDefaultProfileImage() {
        if (this.profileImage == null) {
            this.profileImage = UserConfig.defaultProfileImage;
        }
    }
}

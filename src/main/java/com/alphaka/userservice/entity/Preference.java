package com.alphaka.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Preference extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "preference_id")
    private Long id;

    // 여행 MBTI
    @Enumerated(EnumType.STRING)
    private TripMBTI tripMBTI = TripMBTI.ABLP;

    // 사진 촬영 선호 true = 선호
    @Column(nullable = false)
    private boolean photoPreference = true;

    // 단체 여행 선호 true = 선호
    @Column(nullable = false)
    private boolean groupPreference = true;

    // 도시, 자연 선호 true = 도시, false = 자연
    @Column(nullable = false)
    private boolean regionPreference = true;

    // 새로운 지역 선호 true = 선호
    @Column(nullable = false)
    private boolean noveltyPreference = true;

    //  유명 여행지 선호 true = 선호
    @Column(nullable = false)
    private boolean famePreference = true;

}

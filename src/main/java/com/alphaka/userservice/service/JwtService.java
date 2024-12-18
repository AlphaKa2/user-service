package com.alphaka.userservice.service;

import com.alphaka.userservice.exception.custom.InvalidSmsConfirmationTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    private static final String PHONE_NUMBER_CLAIM = "phoneNumber";

    @Value("${jwt.secretKey}")
    private String secretKey;

    private SecretKey key;

    @PostConstruct
    void initializeKey() {
        key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public void verifySmsConfirmationToken(String token, String phoneNumber) {

        Jws<Claims> claims = getClaimsOrThrow(token);

        verifyPhoneNumber(getTokenPhoneNumberOrThrow(claims), phoneNumber);

        verifyExpiration(claims);
    }

    private Jws<Claims> getClaimsOrThrow(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
        } catch (Exception ex) {
            log.error("sms 인증 토큰 해독 중 오류가 발생했습니다.", ex);
            throw new InvalidSmsConfirmationTokenException();
        }
    }

    private String getTokenPhoneNumberOrThrow(Jws<Claims> claims) {
        try {
            return claims.getPayload()
                    .get(PHONE_NUMBER_CLAIM, String.class);
        } catch (Exception ex) {
            log.error("휴대폰 번호를 추출할 수 없습니다.", ex);
            throw new InvalidSmsConfirmationTokenException();
        }
    }

    private void verifyPhoneNumber(String tokenPhoneNumber, String phoneNumber) {
        if (!tokenPhoneNumber.equals(phoneNumber)) {
            log.error("휴대폰 번호가 일치하지 않습니다");
            throw new InvalidSmsConfirmationTokenException();
        }
    }

    private void verifyExpiration(Jws<Claims> claims) {
        try {
            Date expiration = claims.getPayload().getExpiration();
            Date now = new Date();

            log.info("만료시간: {}, 현재시간: {}", expiration, now);
            if (!expiration.after(now)) {
                throw new JwtException("만료된 토큰");
            }
        } catch (Exception ex) {
            log.error("만료된 토큰입니다.", ex);
            throw new InvalidSmsConfirmationTokenException();
        }

    }


}
package com.quadcore.voiceandtext.application.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TokenService {

    private static final String REFRESH_TOKEN_KEY_PREFIX = "refresh_token:";
    private final StringRedisTemplate stringRedisTemplate;

    public TokenService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * Refresh Token을 Redis에 저장
     */
    public void saveRefreshToken(Long userId, String refreshToken, long expirationMs) {
        String key = REFRESH_TOKEN_KEY_PREFIX + userId;
        stringRedisTemplate.opsForValue().set(
                key,
                refreshToken,
                expirationMs,
                TimeUnit.MILLISECONDS
        );
        log.debug("Saved refresh token for user ID: {}", userId);
    }

    /**
     * Redis에서 Refresh Token 조회
     */
    public String getRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_KEY_PREFIX + userId;
        String token = stringRedisTemplate.opsForValue().get(key);
        log.debug("Retrieved refresh token for user ID: {}", userId);
        return token;
    }

    /**
     * Redis에서 Refresh Token 삭제 (로그아웃/회원탈퇴)
     */
    public void deleteRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_KEY_PREFIX + userId;
        Boolean deleted = stringRedisTemplate.delete(key);
        log.debug("Deleted refresh token for user ID: {}, result: {}", userId, deleted);
    }

    /**
     * 특정 사용자의 모든 토큰 삭제 (회원탈퇴 시)
     */
    public void deleteAllTokens(Long userId) {
        deleteRefreshToken(userId);
    }

    /**
     * Refresh Token 존재 여부 확인
     */
    public boolean existsRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_KEY_PREFIX + userId;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }
}

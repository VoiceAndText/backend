package com.quadcore.voiceandtext.infrastructure.cache;

import com.quadcore.voiceandtext.application.auth.port.RefreshTokenStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis를 이용한 Refresh Token 저장소 구현
 * 
 * RefreshTokenStore 포트의 Redis 구현체입니다.
 * StringRedisTemplate을 사용하여 Redis에 토큰을 저장합니다.
 */
@Slf4j
@Component
public class RedisRefreshTokenStore implements RefreshTokenStore {

    private static final String REFRESH_TOKEN_KEY_PREFIX = "refresh_token:";
    private final StringRedisTemplate stringRedisTemplate;

    public RedisRefreshTokenStore(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void save(Long userId, String refreshToken, long expirationMs) {
        String key = REFRESH_TOKEN_KEY_PREFIX + userId;
        stringRedisTemplate.opsForValue().set(
                key,
                refreshToken,
                expirationMs,
                TimeUnit.MILLISECONDS
        );
        log.debug("Saved refresh token in Redis for user ID: {}", userId);
    }

    @Override
    public String find(Long userId) {
        String key = REFRESH_TOKEN_KEY_PREFIX + userId;
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean delete(Long userId) {
        String key = REFRESH_TOKEN_KEY_PREFIX + userId;
        Boolean deleted = stringRedisTemplate.delete(key);
        return Boolean.TRUE.equals(deleted);
    }

    @Override
    public boolean exists(Long userId) {
        String key = REFRESH_TOKEN_KEY_PREFIX + userId;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }
}

package com.quadcore.voiceandtext.application.auth;

import com.quadcore.voiceandtext.application.auth.port.RefreshTokenStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenService {

    private final RefreshTokenStore refreshTokenStore;

    public TokenService(RefreshTokenStore refreshTokenStore) {
        this.refreshTokenStore = refreshTokenStore;
    }

    /**
     * Refresh Token을 저장소에 저장
     */
    public void saveRefreshToken(Long userId, String refreshToken, long expirationMs) {
        refreshTokenStore.save(userId, refreshToken, expirationMs);
        log.debug("Saved refresh token for user ID: {}", userId);
    }

    /**
     * 저장소에서 Refresh Token 조회
     */
    public String getRefreshToken(Long userId) {
        String token = refreshTokenStore.find(userId);
        log.debug("Retrieved refresh token for user ID: {}", userId);
        return token;
    }

    /**
     * 저장소에서 Refresh Token 삭제 (로그아웃/회원탈퇴)
     */
    public void deleteRefreshToken(Long userId) {
        boolean deleted = refreshTokenStore.delete(userId);
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
        return refreshTokenStore.exists(userId);
    }
}

package com.quadcore.voiceandtext.application.auth.port;

/**
 * Refresh Token 저장소 포트
 * 
 * Refresh Token의 저장, 조회, 삭제 등의 작업을 정의하는 포트 인터페이스입니다.
 * 인프라 계층의 구체적인 구현(Redis, DB 등)으로부터 독립적입니다.
 */
public interface RefreshTokenStore {

    /**
     * Refresh Token을 저장소에 저장합니다.
     * 
     * @param userId 사용자 ID
     * @param refreshToken Refresh Token 값
     * @param expirationMs 만료 시간 (밀리초)
     */
    void save(Long userId, String refreshToken, long expirationMs);

    /**
     * 저장소에서 Refresh Token을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 저장된 Refresh Token, 없으면 null
     */
    String find(Long userId);

    /**
     * 저장소에서 Refresh Token을 삭제합니다.
     * 
     * @param userId 사용자 ID
     * @return 삭제 성공 여부
     */
    boolean delete(Long userId);

    /**
     * 저장소에 Refresh Token이 존재하는지 확인합니다.
     * 
     * @param userId 사용자 ID
     * @return 존재 여부
     */
    boolean exists(Long userId);
}

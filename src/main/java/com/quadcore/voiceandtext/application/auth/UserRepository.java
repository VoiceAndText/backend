package com.quadcore.voiceandtext.application.auth;

import com.quadcore.voiceandtext.domain.user.User;

import java.util.Optional;

/**
 * User 저장소 포트
 * 
 * User의 저장, 조회, 수정 등의 작업을 정의하는 포트 인터페이스입니다.
 * 인프라 계층의 구체적인 구현(JPA, MyBatis 등)으로부터 독립적입니다.
 */
public interface UserRepository {
    
    /**
     * 이메일로 사용자를 조회합니다.
     * 
     * @param email 이메일
     * @return 찾은 사용자, 없으면 빈 Optional
     */
    Optional<User> findByEmail(String email);

    /**
     * Kakao ID로 사용자를 조회합니다.
     * 
     * @param kakaoId 카카오 ID
     * @return 찾은 사용자, 없으면 빈 Optional
     */
    Optional<User> findByKakaoId(String kakaoId);

    /**
     * 사용자 ID로 사용자를 조회합니다.
     * 
     * @param id 사용자 ID
     * @return 찾은 사용자, 없으면 빈 Optional
     */
    Optional<User> findById(Long id);

    /**
     * 사용자를 저장합니다.
     * 
     * @param user 저장할 사용자
     * @return 저장된 사용자
     */
    User save(User user);
}


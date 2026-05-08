package com.quadcore.voiceandtext.infrastructure.persistence;

import com.quadcore.voiceandtext.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA 리포지토리
 * 
 * 이 인터페이스는 Spring Data JPA의 내부 구현 세부사항이며,
 * JpaUserRepository 어댑터를 통해 간접적으로만 사용됩니다.
 */
public interface SpringDataUserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByKakaoId(String kakaoId);
}

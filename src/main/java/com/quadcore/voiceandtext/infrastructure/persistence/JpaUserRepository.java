package com.quadcore.voiceandtext.infrastructure.persistence;

import com.quadcore.voiceandtext.application.auth.UserRepository;
import com.quadcore.voiceandtext.domain.user.User;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Spring Data JPA를 이용한 User 저장소 구현
 * 
 * UserRepository 포트의 JPA 구현체입니다.
 * Spring Data JpaRepository를 사용하여 데이터베이스 액세스를 처리합니다.
 */
@Component
public class JpaUserRepository implements UserRepository {

    private final SpringDataUserJpaRepository springDataUserJpaRepository;

    public JpaUserRepository(SpringDataUserJpaRepository springDataUserJpaRepository) {
        this.springDataUserJpaRepository = springDataUserJpaRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataUserJpaRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByKakaoId(String kakaoId) {
        return springDataUserJpaRepository.findByKakaoId(kakaoId);
    }

    @Override
    public Optional<User> findById(Long id) {
        return springDataUserJpaRepository.findById(id);
    }

    @Override
    public User save(User user) {
        return springDataUserJpaRepository.save(user);
    }
}

package com.quadcore.voiceandtext.infrastructure.persistence;

import com.quadcore.voiceandtext.application.analysis.AnalysisRequestRepository;
import com.quadcore.voiceandtext.domain.analysis.AnalysisRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

interface SpringDataAnalysisRequestJpaRepository extends JpaRepository<AnalysisRequest, Long> {
    @Query("SELECT ar FROM AnalysisRequest ar WHERE ar.isGuest = true AND ar.expiresAt < :expiresAt")
    List<AnalysisRequest> findByIsGuestTrueAndExpiresAtBefore(@Param("expiresAt") LocalDateTime expiresAt);
}

@Component
public class JpaAnalysisRequestRepository implements AnalysisRequestRepository {

    private final SpringDataAnalysisRequestJpaRepository springDataRepository;

    public JpaAnalysisRequestRepository(SpringDataAnalysisRequestJpaRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public AnalysisRequest save(AnalysisRequest analysisRequest) {
        return springDataRepository.save(analysisRequest);
    }

    @Override
    public Optional<AnalysisRequest> findById(Long id) {
        return springDataRepository.findById(id);
    }

    @Override
    public List<AnalysisRequest> findByIsGuestTrueAndExpiresAtBefore(LocalDateTime expiresAt) {
        return springDataRepository.findByIsGuestTrueAndExpiresAtBefore(expiresAt);
    }

    @Override
    public void delete(AnalysisRequest analysisRequest) {
        springDataRepository.delete(analysisRequest);
    }
}
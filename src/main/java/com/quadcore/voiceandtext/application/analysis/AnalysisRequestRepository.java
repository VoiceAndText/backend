package com.quadcore.voiceandtext.application.analysis;

import com.quadcore.voiceandtext.domain.analysis.AnalysisRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AnalysisRequestRepository {
    AnalysisRequest save(AnalysisRequest analysisRequest);
    Optional<AnalysisRequest> findById(Long id);
    List<AnalysisRequest> findByIsGuestTrueAndExpiresAtBefore(LocalDateTime expiresAt);
    void delete(AnalysisRequest analysisRequest);

    Page<AnalysisRequest> findAll(Pageable pageable);

    Page<AnalysisRequest> findByUserId(Long userId, Pageable pageable);
}
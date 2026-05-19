package com.quadcore.voiceandtext.application.analysis;

import com.quadcore.voiceandtext.domain.analysis.AnalysisRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AnalysisRequestRepository {
    AnalysisRequest save(AnalysisRequest analysisRequest);
    Optional<AnalysisRequest> findById(Long id);
    List<AnalysisRequest> findByIsGuestTrueAndExpiresAtBefore(LocalDateTime expiresAt);
    void delete(AnalysisRequest analysisRequest);
}
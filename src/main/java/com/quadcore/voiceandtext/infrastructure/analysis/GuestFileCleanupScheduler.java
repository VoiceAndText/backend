package com.quadcore.voiceandtext.infrastructure.analysis;

import com.quadcore.voiceandtext.application.analysis.AnalysisRequestRepository;
import com.quadcore.voiceandtext.domain.analysis.AnalysisRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GuestFileCleanupScheduler {

    private final AnalysisRequestRepository analysisRequestRepository;
    private final S3Service s3Service;

    @Scheduled(fixedRateString = "${app.cleanup.interval:3600000}") // 기본 1시간
    @Transactional
    public void cleanupExpiredGuestFiles() {
        LocalDateTime now = LocalDateTime.now();
        List<AnalysisRequest> expiredRequests = analysisRequestRepository.findByIsGuestTrueAndExpiresAtBefore(now);

        for (AnalysisRequest request : expiredRequests) {
            try {
                // S3 파일 삭제
                if (request.getAudioFile() != null) {
                    s3Service.deleteFile(request.getAudioFile().getStorageLocation());
                }

                // DB에서 삭제 또는 만료 처리
                analysisRequestRepository.delete(request);

                log.info("만료된 비회원 파일 삭제: {}", request.getId());
            } catch (Exception e) {
                log.error("비회원 파일 삭제 실패: {}", request.getId(), e);
            }
        }
    }
}
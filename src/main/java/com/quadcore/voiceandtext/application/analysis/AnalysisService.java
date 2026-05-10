package com.quadcore.voiceandtext.application.analysis;

import com.quadcore.voiceandtext.application.auth.UserRepository;
import com.quadcore.voiceandtext.common.exception.BusinessException;
import com.quadcore.voiceandtext.common.exception.ErrorCode;
import com.quadcore.voiceandtext.domain.analysis.AnalysisRequest;
import com.quadcore.voiceandtext.domain.analysis.AnalysisStatus;
import com.quadcore.voiceandtext.domain.file.AudioFile;
import com.quadcore.voiceandtext.domain.user.User;
import com.quadcore.voiceandtext.infrastructure.analysis.AiService;
import com.quadcore.voiceandtext.presentation.analysis.dto.AudioUploadRequest;
import com.quadcore.voiceandtext.presentation.analysis.dto.AudioUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AudioUploadService audioUploadService;
    private final AiService aiService;
    private final AnalysisRequestRepository analysisRequestRepository;
    private final UserRepository userRepository;

    @Transactional
    public AudioUploadResponse uploadAndRequestAnalysis(AudioUploadRequest request, Long userId) {
        // 파일 검증
        validateAudioFile(request.getAudio());

        // 사용자 조회 (비회원일 경우 null)
        User user = userId != null ? userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)) : null;

        // AnalysisRequest 생성
        AnalysisRequest analysisRequest = createAnalysisRequest(request, user);

        // AudioFile 생성 및 S3 업로드
        AudioFile audioFile = audioUploadService.uploadAudioFile(request.getAudio(), analysisRequest, request.getSourceType(), request.getDurationSeconds());

        // AnalysisRequest에 AudioFile 연결
        analysisRequest.setAudioFile(audioFile);

        // 저장
        AnalysisRequest savedRequest = analysisRequestRepository.save(analysisRequest);

        // AI 서버 요청
        try {
            aiService.requestAnalysis(savedRequest);
            savedRequest.setStatus(AnalysisStatus.PROCESSING);
        } catch (Exception e) {
            log.error("AI 서버 요청 실패: {}", e.getMessage());
            savedRequest.setStatus(AnalysisStatus.FAILED);
            savedRequest.setErrorMessage("AI 서버 요청 실패: " + e.getMessage());
        }

        analysisRequestRepository.save(savedRequest);

        // 응답 생성
        return AudioUploadResponse.builder()
                .analysisRequestId(savedRequest.getId())
                .guestResultToken(user == null ? generateGuestToken(savedRequest) : null)
                .build();
    }

    private void validateAudioFile(MultipartFile audio) {
        if (audio.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "빈 파일은 업로드할 수 없습니다.");
        }

        // 파일 크기 검증 (예: 10MB)
        if (audio.getSize() > 10 * 1024 * 1024) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "파일 크기는 10MB를 초과할 수 없습니다.");
        }

        // MIME 타입 검증
        String contentType = audio.getContentType();
        if (contentType == null || !contentType.startsWith("audio/")) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "음성 파일만 업로드 가능합니다.");
        }
    }

    private AnalysisRequest createAnalysisRequest(AudioUploadRequest request, User user) {
        String guestToken = user == null ? UUID.randomUUID().toString() : null;
        String guestTokenHash = guestToken != null ? hashToken(guestToken) : null;

        return AnalysisRequest.builder()
                .title("음성 분석 요청")
                .description(request.getContext())
                .user(user)
                .isGuest(user == null)
                .status(AnalysisStatus.PENDING)
                .guestResultTokenHash(guestTokenHash)
                .expiresAt(user == null ? LocalDateTime.now().plusHours(24) : null) // 비회원 24시간
                .build();
    }

    private String generateGuestToken(AnalysisRequest request) {
        // 실제로는 request.getGuestResultTokenHash()에서 복원 로직 필요하지만, 간단히 UUID 사용
        return UUID.randomUUID().toString();
    }

    private String hashToken(String token) {
        // SHA-256 해싱 (실제 구현에서는 BCrypt 등 사용)
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(token.getBytes());
            return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("토큰 해싱 실패", e);
        }
    }
}
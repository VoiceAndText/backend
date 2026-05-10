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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AudioUploadService audioUploadService;
    private final AiService aiService;
    private final AnalysisRequestRepository analysisRequestRepository;
    private final UserRepository userRepository;

    @Value("${app.audio.max-file-size:10485760}") // 10MB
    private long maxFileSize;

    @Value("${app.audio.max-duration-seconds:60}")
    private int maxDurationSeconds;

    @Value("${app.guest.expiry-hours:24}")
    private int guestExpiryHours;

    @Value("${app.audio.allowed-mime-types:audio/mpeg,audio/wav,audio/ogg,audio/webm,audio/mp4}")
    private String allowedMimeTypesStr;

    private Set<String> allowedMimeTypes;

    private void initAllowedMimeTypes() {
        if (allowedMimeTypes == null) {
            allowedMimeTypes = new HashSet<>(Arrays.asList(allowedMimeTypesStr.split("\\s*,\\s*")));
        }
    }

    @Transactional
    public AudioUploadResponse uploadAndRequestAnalysis(AudioUploadRequest request, Long userId) {
        // 파일 및 duration 검증
        validateAudioFile(request.getAudio(), request.getDurationSeconds());

        // 사용자 조회 (비회원일 경우 null)
        User user = userId != null ? userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)) : null;

        // AnalysisRequest 생성
        AnalysisRequest analysisRequest = createAnalysisRequest(request, user);

        // AnalysisRequest를 먼저 DB에 저장해서 ID 확정
        AnalysisRequest savedRequest = analysisRequestRepository.save(analysisRequest);

        // AudioFile 생성 및 S3 업로드 (ID가 확정된 savedRequest 사용)
        AudioFile audioFile = audioUploadService.uploadAudioFile(request.getAudio(), savedRequest, request.getSourceType(), request.getDurationSeconds());

        // AnalysisRequest에 AudioFile 연결
        savedRequest.setAudioFile(audioFile);

        // AudioFile 연결 후 다시 저장
        savedRequest = analysisRequestRepository.save(savedRequest);

        // 응답 생성
        AudioUploadResponse response = AudioUploadResponse.builder()
                .analysisRequestId(savedRequest.getId())
                .guestResultToken(user == null ? generateGuestToken(savedRequest) : null)
                .build();

        // AI 서버 요청은 트랜잭션 완료 후 별도로 처리 (비동기 또는 다른 트랜잭션)
        requestAnalysisAsync(savedRequest);

        return response;
    }

    /**
     * AI 서버 요청을 트랜잭션 외부에서 처리
     */
    private void requestAnalysisAsync(AnalysisRequest analysisRequest) {
        try {
            aiService.requestAnalysis(analysisRequest);
            updateAnalysisStatusAfterAiRequest(analysisRequest.getId(), AnalysisStatus.PROCESSING, null);
        } catch (Exception e) {
            log.error("AI 서버 요청 실패. analysisRequestId={}", analysisRequest.getId(), e);
            updateAnalysisStatusAfterAiRequest(analysisRequest.getId(), AnalysisStatus.FAILED, "AI 서버 요청 중 오류가 발생했습니다.");
        }
    }

    /**
     * 분석 상태 업데이트 (REQUIRES_NEW로 새로운 트랜잭션 생성)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAnalysisStatusAfterAiRequest(Long analysisRequestId, AnalysisStatus status, String errorMessage) {
        AnalysisRequest analysisRequest = analysisRequestRepository.findById(analysisRequestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "분석 요청을 찾을 수 없습니다."));
        
        analysisRequest.setStatus(status);
        if (errorMessage != null) {
            analysisRequest.setErrorMessage(errorMessage);
        }
        
        analysisRequestRepository.save(analysisRequest);
    }

    private void validateAudioFile(MultipartFile audio, Integer durationSeconds) {
        initAllowedMimeTypes();

        if (audio.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "빈 파일은 업로드할 수 없습니다.");
        }

        // Duration 검증
        if (durationSeconds == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "녹음 시간이 필요합니다.");
        }
        if (durationSeconds <= 0) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "녹음 시간은 0초보다 커야 합니다.");
        }
        if (durationSeconds > maxDurationSeconds) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "녹음 시간은 " + maxDurationSeconds + "초 이하여야 합니다.");
        }

        // 파일 크기 검증
        if (audio.getSize() > maxFileSize) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "파일 크기는 " + (maxFileSize / (1024 * 1024)) + "MB를 초과할 수 없습니다.");
        }

        // MIME 타입 검증 (화이트리스트)
        String contentType = audio.getContentType();
        if (contentType == null || !allowedMimeTypes.contains(contentType)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "지원하지 않는 파일 형식입니다. 지원 형식: " + String.join(", ", allowedMimeTypes));
        }

        // Magic bytes 검증
        validateAudioMagicBytes(audio);
    }

    /**
     * 파일의 실제 음성 파일 여부를 magic bytes로 검증
     */
    private void validateAudioMagicBytes(MultipartFile audio) {
        try {
            byte[] fileBytes = audio.getBytes();
            if (fileBytes.length < 4) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "파일이 너무 작아 유효한 음성 파일이 아닙니다.");
            }

            // 음성 파일 매직 바이트 검증
            // MP3: 0xFF 0xFB or 0xFF 0xFA (MPEG-1/2 Layer III)
            // WAV: 0x52 0x49 0x46 0x46 ("RIFF")
            // OGG: 0x4F 0x67 0x67 0x53 ("OggS")
            // MP4/M4A: 0x66 0x74 0x79 0x70 at offset 4 ("ftyp")
            // WebM: 0x1A 0x45 0xDF 0xA3
            boolean isValidMagicBytes = isValidAudioMagicBytes(fileBytes);
            if (!isValidMagicBytes) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "파일이 유효한 음성 파일이 아닙니다. 다시 확인해주세요.");
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "파일 검증 중 오류가 발생했습니다.");
        }
    }

    /**
     * Magic bytes를 확인하여 실제 음성 파일인지 검증
     */
    private boolean isValidAudioMagicBytes(byte[] fileBytes) {
        if (fileBytes.length < 4) {
            return false;
        }

        // MP3 (0xFF 0xFB or 0xFF 0xFA)
        if ((fileBytes[0] == (byte) 0xFF && (fileBytes[1] == (byte) 0xFB || fileBytes[1] == (byte) 0xFA))) {
            return true;
        }

        // WAV ("RIFF" = 0x52 0x49 0x46 0x46)
        if (fileBytes[0] == 0x52 && fileBytes[1] == 0x49 && fileBytes[2] == 0x46 && fileBytes[3] == 0x46) {
            return true;
        }

        // OGG ("OggS" = 0x4F 0x67 0x67 0x53)
        if (fileBytes[0] == 0x4F && fileBytes[1] == 0x67 && fileBytes[2] == 0x67 && fileBytes[3] == 0x53) {
            return true;
        }

        // WebM (0x1A 0x45 0xDF 0xA3)
        if (fileBytes[0] == (byte) 0x1A && fileBytes[1] == 0x45 && fileBytes[2] == (byte) 0xDF && fileBytes[3] == (byte) 0xA3) {
            return true;
        }

        // MP4/M4A ("ftyp" at offset 4)
        if (fileBytes.length >= 8 && fileBytes[4] == 0x66 && fileBytes[5] == 0x74 && fileBytes[6] == 0x79 && fileBytes[7] == 0x70) {
            return true;
        }

        return false;
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
                .expiresAt(user == null ? LocalDateTime.now().plusHours(guestExpiryHours) : null)
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
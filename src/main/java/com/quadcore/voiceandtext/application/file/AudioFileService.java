package com.quadcore.voiceandtext.application.file;

import com.quadcore.voiceandtext.application.analysis.AnalysisRequestRepository;
import com.quadcore.voiceandtext.application.analysis.FileStoragePort;
import com.quadcore.voiceandtext.common.exception.BusinessException;
import com.quadcore.voiceandtext.common.exception.ErrorCode;
import com.quadcore.voiceandtext.domain.analysis.AnalysisRequest;
import com.quadcore.voiceandtext.domain.file.AudioFile;
import com.quadcore.voiceandtext.presentation.file.dto.AudioFileResponse;
import com.quadcore.voiceandtext.presentation.file.dto.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AudioFileService {

    private static final int PRESIGNED_URL_EXPIRY_SECONDS = 900; // 15분

    private final AnalysisRequestRepository analysisRequestRepository;
    private final FileStoragePort fileStoragePort;

    @Transactional(readOnly = true)
    public Page<AudioFileResponse> getMyAudioFiles(Long userId, Pageable pageable) {
        requireAuth(userId);
        return analysisRequestRepository.findByUserIdWithAudioFile(userId, pageable)
                .map(req -> AudioFileResponse.from(req, req.getAudioFile()));
    }

    @Transactional(readOnly = true)
    public PresignedUrlResponse getPresignedUrl(Long userId, Long analysisRequestId) {
        requireAuth(userId);
        AudioFile audioFile = getOwnedAudioFile(userId, analysisRequestId);
        String url = fileStoragePort.generatePresignedUrl(
                audioFile.getStorageLocation(), Duration.ofSeconds(PRESIGNED_URL_EXPIRY_SECONDS));
        return new PresignedUrlResponse(url, PRESIGNED_URL_EXPIRY_SECONDS);
    }

    @Transactional
    public void deleteAudioFile(Long userId, Long analysisRequestId) {
        requireAuth(userId);
        AnalysisRequest req = getOwnedAnalysisRequest(userId, analysisRequestId);
        AudioFile audioFile = req.getAudioFile();
        if (audioFile == null) {
            throw new BusinessException(ErrorCode.AUDIO_FILE_NOT_FOUND);
        }
        fileStoragePort.deleteFile(audioFile.getStorageLocation());
        req.setAudioFile(null);
        analysisRequestRepository.save(req);
    }

    private AudioFile getOwnedAudioFile(Long userId, Long analysisRequestId) {
        AnalysisRequest req = getOwnedAnalysisRequest(userId, analysisRequestId);
        AudioFile audioFile = req.getAudioFile();
        if (audioFile == null) {
            throw new BusinessException(ErrorCode.AUDIO_FILE_NOT_FOUND);
        }
        return audioFile;
    }

    private AnalysisRequest getOwnedAnalysisRequest(Long userId, Long analysisRequestId) {
        AnalysisRequest req = analysisRequestRepository.findById(analysisRequestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "분석 요청을 찾을 수 없습니다."));
        if (Boolean.TRUE.equals(req.getIsGuest())
                || req.getUser() == null
                || !req.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 파일만 접근할 수 있습니다.");
        }
        return req;
    }

    private void requireAuth(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증이 필요합니다.");
        }
    }
}

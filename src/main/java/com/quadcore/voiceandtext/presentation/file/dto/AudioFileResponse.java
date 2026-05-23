package com.quadcore.voiceandtext.presentation.file.dto;

import com.quadcore.voiceandtext.domain.analysis.AnalysisRequest;
import com.quadcore.voiceandtext.domain.file.AudioFile;
import com.quadcore.voiceandtext.domain.file.FileSourceType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AudioFileResponse {
    private Long analysisRequestId;
    private String originalFileName;
    private Long fileSizeBytes;
    private Integer durationSeconds;
    private String mimeType;
    private FileSourceType sourceType;
    private LocalDateTime uploadedAt;

    public static AudioFileResponse from(AnalysisRequest analysisRequest, AudioFile audioFile) {
        return AudioFileResponse.builder()
                .analysisRequestId(analysisRequest.getId())
                .originalFileName(audioFile.getOriginalFileName())
                .fileSizeBytes(audioFile.getFileSizeBytes())
                .durationSeconds(audioFile.getDurationSeconds())
                .mimeType(audioFile.getMimeType())
                .sourceType(audioFile.getSourceType())
                .uploadedAt(audioFile.getCreatedAt())
                .build();
    }
}

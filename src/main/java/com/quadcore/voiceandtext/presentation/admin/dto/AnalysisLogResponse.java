package com.quadcore.voiceandtext.presentation.admin.dto;

import com.quadcore.voiceandtext.domain.analysis.AnalysisRequest;
import com.quadcore.voiceandtext.domain.analysis.AnalysisStatus;
import com.quadcore.voiceandtext.domain.file.FileSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class AnalysisLogResponse {
    private Long analysisRequestId;
    private Long userId;
    private boolean isGuest;
    private AnalysisStatus status;
    private FileSourceType sourceType;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AnalysisLogResponse from(AnalysisRequest ar) {
        FileSourceType sourceType = null;
        if (ar.getAudioFile() != null) {
            sourceType = ar.getAudioFile().getSourceType();
        }
        return AnalysisLogResponse.builder()
                .analysisRequestId(ar.getId())
                .userId(ar.getUser() != null ? ar.getUser().getId() : null)
                .isGuest(ar.getIsGuest())
                .status(ar.getStatus())
                .sourceType(sourceType)
                .errorMessage(ar.getErrorMessage())
                .createdAt(ar.getCreatedAt())
                .updatedAt(ar.getUpdatedAt())
                .build();
    }
}

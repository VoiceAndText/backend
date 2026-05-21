package com.quadcore.voiceandtext.presentation.analysis.dto;

public record AnalysisRequestStatusResponse(
        Long analysisRequestId,
        String status,
        String message,
        String errorMessage,
        AnalysisResultResponse result
) {
}

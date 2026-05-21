package com.quadcore.voiceandtext.presentation.analysis.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record AnalysisResultResponse(
        String primaryEmotion,
        Double dissonanceIndex,
        String summaryExplanation,
        JsonNode timeSeriesAnalysis
) {
}

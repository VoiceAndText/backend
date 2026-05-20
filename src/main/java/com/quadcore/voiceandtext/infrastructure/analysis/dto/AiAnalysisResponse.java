package com.quadcore.voiceandtext.infrastructure.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AiAnalysisResponse {
    private String status;
    private String message;
    private DataPayload data;

    @Data
    public static class DataPayload {
        @JsonProperty("overall_analysis")
        private OverallAnalysis overallAnalysis;

        @JsonProperty("time_series_analysis")
        private List<Map<String, Object>> timeSeriesAnalysis;
    }

    @Data
    public static class OverallAnalysis {
        @JsonProperty("primary_emotion")
        private String primaryEmotion;

        @JsonProperty("dissonance_index")
        private Double dissonanceIndex;
    }
}

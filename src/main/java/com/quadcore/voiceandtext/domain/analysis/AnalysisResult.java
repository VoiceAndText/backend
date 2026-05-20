package com.quadcore.voiceandtext.domain.analysis;

import com.quadcore.voiceandtext.common.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "analysis_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResult extends BaseTimeEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private EmotionType textEmotion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private EmotionType voiceEmotion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private EmotionType finalEmotion;

    @Column(nullable = true)
    private Double textEmotionScore;

    @Column(nullable = true)
    private Double voiceEmotionScore;

    @Column(nullable = true)
    private Double mismatchScore;

    @Column(length = 255)
    private String primaryEmotion;

    @Column
    private Double dissonanceIndex;

    @Column(columnDefinition = "TEXT")
    private String timeSeriesAnalysis;

    @Column(columnDefinition = "TEXT")
    private String summaryExplanation;

    @Column(columnDefinition = "TEXT")
    private String detailedAnalysis;

    @Column
    private Long processingTimeMs;
}

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
    @Column(nullable = false)
    private EmotionType textEmotion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmotionType voiceEmotion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmotionType finalEmotion;

    @Column(nullable = false)
    private Double textEmotionScore;

    @Column(nullable = false)
    private Double voiceEmotionScore;

    @Column(nullable = false)
    private Double mismatchScore;

    @Column(columnDefinition = "TEXT")
    private String summaryExplanation;

    @Column(columnDefinition = "TEXT")
    private String detailedAnalysis;

    @Column
    private Long processingTimeMs;
}

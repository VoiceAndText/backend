package com.quadcore.voiceandtext.domain.analysis;

import com.quadcore.voiceandtext.common.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "analysis_timeline_segments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisTimelineSegment extends BaseTimeEntity {
    @Column(nullable = false)
    private Integer startTimeMs;

    @Column(nullable = false)
    private Integer endTimeMs;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmotionType detectedEmotion;

    @Column(nullable = false)
    private Double emotionScore;

    @Column(columnDefinition = "TEXT")
    private String transcriptionSegment;

    @Column(nullable = false)
    private Long analysisResultId;
}

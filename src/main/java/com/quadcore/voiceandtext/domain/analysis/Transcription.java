package com.quadcore.voiceandtext.domain.analysis;

import com.quadcore.voiceandtext.common.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transcriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transcription extends BaseTimeEntity {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false)
    private String language;

    @Column
    private Double confidence;

    @Column(length = 100)
    private String transcriptionProvider;

    @Column
    private Long processingTimeMs;
}

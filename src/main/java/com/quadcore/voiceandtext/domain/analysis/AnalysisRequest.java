package com.quadcore.voiceandtext.domain.analysis;

import com.quadcore.voiceandtext.common.base.BaseTimeEntity;
import com.quadcore.voiceandtext.domain.file.AudioFile;
import com.quadcore.voiceandtext.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "analysis_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisRequest extends BaseTimeEntity {
    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(nullable = false)
    private Boolean isGuest;

    @Column(length = 100)
    private String guestEmail;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "audio_file_id", nullable = false)
    private AudioFile audioFile;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "transcription_id", nullable = false)
    private Transcription transcription;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "analysis_result_id")
    private AnalysisResult analysisResult;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisType analysisType;

    @Column
    private Integer priority;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;
}

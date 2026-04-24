package com.quadcore.voiceandtext.domain.file;

import com.quadcore.voiceandtext.common.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "audio_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AudioFile extends BaseTimeEntity {
    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false, unique = true)
    private String storedFileName;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private Long fileSizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType fileType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileSourceType sourceType;

    @Column(length = 50)
    private String mimeType;

    @Column
    private Integer durationSeconds;

    @Column(length = 255)
    private String storageLocation;
}

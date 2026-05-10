package com.quadcore.voiceandtext.presentation.analysis.dto;

import com.quadcore.voiceandtext.domain.file.FileSourceType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AudioUploadRequest {
    @NotNull(message = "음성 파일은 필수입니다.")
    private MultipartFile audio;

    @NotNull(message = "소스 타입은 필수입니다.")
    private FileSourceType sourceType;

    @NotNull(message = "녹음 시간은 필수입니다.")
    @Min(value = 1, message = "녹음 시간은 1초 이상이어야 합니다.")
    @Max(value = 60, message = "녹음 시간은 60초 이하여야 합니다.")
    private Integer durationSeconds;

    private String context;
}

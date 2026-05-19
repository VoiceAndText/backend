package com.quadcore.voiceandtext.presentation.analysis;

import com.quadcore.voiceandtext.application.analysis.AnalysisService;
import com.quadcore.voiceandtext.common.exception.BusinessException;
import com.quadcore.voiceandtext.common.exception.ErrorCode;
import com.quadcore.voiceandtext.common.response.ApiResponse;
import com.quadcore.voiceandtext.domain.file.FileSourceType;
import com.quadcore.voiceandtext.presentation.analysis.dto.AudioUploadRequest;
import com.quadcore.voiceandtext.presentation.analysis.dto.AudioUploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/analysis")
@Tag(name = "Analysis", description = "음성 분석 관련 API")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;
    private FileSourceType parseSourceType(String sourceType) {
        if (sourceType == null || sourceType.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_FILE_SOURCE_TYPE);
        }

        try {
            return FileSourceType.valueOf(sourceType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_FILE_SOURCE_TYPE);
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "음성 파일 업로드 및 분석 요청", description = "음성 파일을 업로드하고 분석을 요청합니다. 회원/비회원 모두 가능합니다.")
    public ResponseEntity<ApiResponse<AudioUploadResponse>> uploadAudio(
            @RequestParam("audio") MultipartFile audio,
            @RequestParam("sourceType") FileSourceType sourceType,
            @RequestParam("durationSeconds") Integer durationSeconds,
            @RequestParam(value = "context", required = false) String context,
            @AuthenticationPrincipal Long userId) {

        AudioUploadRequest request = AudioUploadRequest.builder()
                .audio(audio)
                .sourceType(sourceType)
                .durationSeconds(durationSeconds)
                .context(context)
                .build();

        AudioUploadResponse response = analysisService.uploadAndRequestAnalysis(request, userId);
        return ResponseEntity.ok(ApiResponse.success("음성 분석 요청이 접수되었습니다.", response));
    }
}

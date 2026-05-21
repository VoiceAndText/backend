package com.quadcore.voiceandtext.presentation.analysis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadcore.voiceandtext.application.analysis.AnalysisService;
import com.quadcore.voiceandtext.common.exception.BusinessException;
import com.quadcore.voiceandtext.common.exception.ErrorCode;
import com.quadcore.voiceandtext.common.response.ApiResponse;
import com.quadcore.voiceandtext.domain.analysis.AnalysisRequest;
import com.quadcore.voiceandtext.domain.analysis.AnalysisResult;
import com.quadcore.voiceandtext.domain.file.FileSourceType;
import com.quadcore.voiceandtext.presentation.analysis.dto.AnalysisRequestStatusResponse;
import com.quadcore.voiceandtext.presentation.analysis.dto.AnalysisResultResponse;
import com.quadcore.voiceandtext.presentation.analysis.dto.AudioUploadRequest;
import com.quadcore.voiceandtext.presentation.analysis.dto.AudioUploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    private final ObjectMapper objectMapper;

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

    @GetMapping("/guest/{analysisRequestId}")
    @Operation(summary = "비회원 분석 결과 조회", description = "guestResultToken을 검증하여 분석 상태와 결과를 조회합니다.")
    public ResponseEntity<ApiResponse<AnalysisRequestStatusResponse>> getGuestAnalysisResult(
            @PathVariable Long analysisRequestId,
            @Parameter(description = "비회원 결과 조회 토큰", required = true)
            @RequestParam("token") String guestResultToken) {

        AnalysisRequest analysisRequest = analysisService.getAnalysisRequestForGuest(analysisRequestId, guestResultToken);
        AnalysisRequestStatusResponse response = buildStatusResponse(analysisRequest);
        return ResponseEntity.ok(ApiResponse.success("분석 결과 조회에 성공했습니다.", response));
    }

    @GetMapping("/{analysisRequestId}")
    @Operation(summary = "회원 분석 결과 조회", description = "JWT 인증된 사용자 본인의 분석 요청만 조회할 수 있습니다.")
    public ResponseEntity<ApiResponse<AnalysisRequestStatusResponse>> getMemberAnalysisResult(
            @PathVariable Long analysisRequestId,
            @AuthenticationPrincipal Long userId) {

        AnalysisRequest analysisRequest = analysisService.getAnalysisRequestForUser(analysisRequestId, userId);
        AnalysisRequestStatusResponse response = buildStatusResponse(analysisRequest);
        return ResponseEntity.ok(ApiResponse.success("분석 결과 조회에 성공했습니다.", response));
    }

    private AnalysisRequestStatusResponse buildStatusResponse(AnalysisRequest analysisRequest) {
        String status = analysisRequest.getStatus().name();
        String message = buildStatusMessage(analysisRequest.getStatus());
        String errorMessage = null;
        if (analysisRequest.getStatus() == com.quadcore.voiceandtext.domain.analysis.AnalysisStatus.FAILED) {
            errorMessage = analysisRequest.getErrorMessage() != null ? analysisRequest.getErrorMessage() : "분석에 실패했습니다.";
        }

        AnalysisResultResponse result = null;
        if (analysisRequest.getStatus() == com.quadcore.voiceandtext.domain.analysis.AnalysisStatus.COMPLETED) {
            result = buildResultResponse(analysisRequest.getAnalysisResult());
        }

        return new AnalysisRequestStatusResponse(
                analysisRequest.getId(),
                status,
                message,
                errorMessage,
                result
        );
    }

    private String buildStatusMessage(com.quadcore.voiceandtext.domain.analysis.AnalysisStatus status) {
        return switch (status) {
            case PENDING, PROCESSING -> "분석이 진행 중입니다.";
            case COMPLETED -> "분석이 완료되었습니다.";
            case FAILED -> "분석에 실패했습니다.";
        };
    }

    private AnalysisResultResponse buildResultResponse(AnalysisResult analysisResult) {
        if (analysisResult == null) {
            return null;
        }

        JsonNode timeSeriesAnalysis = parseTimeSeriesAnalysis(analysisResult.getTimeSeriesAnalysis());

        return new AnalysisResultResponse(
                analysisResult.getPrimaryEmotion(),
                analysisResult.getDissonanceIndex(),
                analysisResult.getSummaryExplanation(),
                timeSeriesAnalysis
        );
    }

    private JsonNode parseTimeSeriesAnalysis(String rawJson) {
        if (rawJson == null || rawJson.isBlank()) {
            return objectMapper.createArrayNode();
        }

        try {
            JsonNode parsed = objectMapper.readTree(rawJson);
            return parsed == null || parsed.isNull() ? objectMapper.createArrayNode() : parsed;
        } catch (Exception e) {
            log.warn("timeSeriesAnalysis JSON 파싱 실패: {}", e.getMessage());
            return objectMapper.createArrayNode();
        }
    }
}


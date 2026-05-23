package com.quadcore.voiceandtext.presentation.file;

import com.quadcore.voiceandtext.application.file.AudioFileService;
import com.quadcore.voiceandtext.common.response.ApiResponse;
import com.quadcore.voiceandtext.presentation.file.dto.AudioFileResponse;
import com.quadcore.voiceandtext.presentation.file.dto.PresignedUrlResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "AudioFile", description = "음성 파일 관리 API (회원 전용)")
@SecurityRequirement(name = "bearer-jwt")
@RequiredArgsConstructor
public class AudioFileController {

    private final AudioFileService audioFileService;

    @GetMapping
    @Operation(summary = "내 음성 파일 목록 조회", description = "본인이 업로드한 음성 파일 목록을 페이지네이션으로 조회합니다.")
    public ResponseEntity<ApiResponse<Page<AudioFileResponse>>> getMyAudioFiles(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AudioFileResponse> files = audioFileService.getMyAudioFiles(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(files));
    }

    @GetMapping("/{analysisRequestId}/presigned-url")
    @Operation(summary = "음성 파일 재생 링크 발급", description = "S3 Presigned URL(15분 유효)을 발급합니다. 본인 파일만 접근 가능합니다.")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> getPresignedUrl(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long analysisRequestId) {
        PresignedUrlResponse response = audioFileService.getPresignedUrl(userId, analysisRequestId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{analysisRequestId}")
    @Operation(summary = "음성 파일 삭제", description = "S3에서 파일을 삭제하고 연결을 해제합니다. 본인 파일만 삭제 가능하며, 분석 결과는 유지됩니다.")
    public ResponseEntity<ApiResponse<Void>> deleteAudioFile(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long analysisRequestId) {
        audioFileService.deleteAudioFile(userId, analysisRequestId);
        return ResponseEntity.ok(ApiResponse.success("음성 파일이 삭제되었습니다."));
    }
}

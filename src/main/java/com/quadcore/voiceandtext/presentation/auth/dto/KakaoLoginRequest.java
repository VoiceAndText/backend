package com.quadcore.voiceandtext.presentation.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "카카오 로그인 요청 DTO")
public class KakaoLoginRequest {
    @NotBlank(message = "Authorization code cannot be null or blank")
    @Schema(description = "카카오 인가 코드", example = "abc123def456...")
    private String code;
}

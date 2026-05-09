package com.quadcore.voiceandtext.presentation.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "토큰 재발급 응답 DTO")
public class TokenRefreshResponse {
    @Schema(description = "새로운 Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
    
    @Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType;
    
    @Schema(description = "토큰 만료 시간 (초)", example = "3600")
    private long expiresIn;
}

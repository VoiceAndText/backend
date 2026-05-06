package com.quadcore.voiceandtext.presentation.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private Long userId;
    private String email;
    private String name;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
}

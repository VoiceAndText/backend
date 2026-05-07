package com.quadcore.voiceandtext.presentation.auth;

import com.quadcore.voiceandtext.application.auth.AuthService;
import com.quadcore.voiceandtext.common.exception.BusinessException;
import com.quadcore.voiceandtext.common.exception.ErrorCode;
import com.quadcore.voiceandtext.common.response.ApiResponse;
import com.quadcore.voiceandtext.presentation.auth.dto.AuthResponse;
import com.quadcore.voiceandtext.presentation.auth.dto.KakaoLoginRequest;
import com.quadcore.voiceandtext.presentation.auth.dto.TokenRefreshRequest;
import com.quadcore.voiceandtext.presentation.auth.dto.TokenRefreshResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 카카오 로그인/회원가입
     */
    @PostMapping("/kakao-login")
    public ResponseEntity<ApiResponse<AuthResponse>> kakaoLogin(
            @RequestBody KakaoLoginRequest request) {
        AuthResponse response = authService.kakaoLogin(request.getAccessToken());
        return ResponseEntity.ok(ApiResponse.success("카카오 로그인에 성공했습니다.", response));
    }

    /**
     * Access Token 재발급
     */
    @PostMapping("/token-refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshAccessToken(
            @Valid @RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse response = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("토큰이 재발급되었습니다.", response));
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증 정보가 없습니다.");
        }
        authService.logout(userId);
        return ResponseEntity.ok(ApiResponse.success("로그아웃되었습니다."));
    }

    /**
     * 회원탈퇴
     */
    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdraw(@AuthenticationPrincipal Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증 정보가 없습니다.");
        }
        authService.withdraw(userId);
        return ResponseEntity.ok(ApiResponse.success("회원탈퇴되었습니다."));
    }
}

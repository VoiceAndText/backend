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
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 카카오 로그인/회원가입
     */
    @PostMapping("/kakao-login")
    @Operation(summary = "카카오 로그인/회원가입", description = "카카오 인가 코드를 사용하여 로그인 또는 회원가입합니다. OAuth2 Authorization Code Flow를 사용합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인/회원가입 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 - 인가 코드가 없거나 유효하지 않음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> kakaoLogin(
            @Valid @RequestBody KakaoLoginRequest request) {
        AuthResponse response = authService.kakaoLogin(request.getCode());
        return ResponseEntity.ok(ApiResponse.success("카카오 로그인에 성공했습니다.", response));
    }

    /**
     * Access Token 재발급
     */
    @PostMapping("/token-refresh")
    @Operation(summary = "Access Token 재발급", description = "Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenRefreshResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 - Refresh Token이 없거나 유효하지 않음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshAccessToken(
            @Valid @RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse response = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("토큰이 재발급되었습니다.", response));
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 사용자를 로그아웃합니다. 인증이 필요합니다.")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패 - 유효한 토큰이 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
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
    @Operation(summary = "회원탈퇴", description = "현재 사용자의 계정을 탈퇴합니다. 인증이 필요합니다.")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원탈퇴 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패 - 유효한 토큰이 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<ApiResponse<Void>> withdraw(@AuthenticationPrincipal Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증 정보가 없습니다.");
        }
        authService.withdraw(userId);
        return ResponseEntity.ok(ApiResponse.success("회원탈퇴되었습니다."));
    }
}

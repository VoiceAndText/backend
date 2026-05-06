package com.quadcore.voiceandtext.application.auth;

import com.quadcore.voiceandtext.common.exception.BusinessException;
import com.quadcore.voiceandtext.common.exception.ErrorCode;
import com.quadcore.voiceandtext.domain.user.User;
import com.quadcore.voiceandtext.domain.user.UserRole;
import com.quadcore.voiceandtext.domain.user.UserStatus;
import com.quadcore.voiceandtext.infrastructure.oauth.KakaoOAuthService;
import com.quadcore.voiceandtext.infrastructure.oauth.KakaoUserInfoResponse;
import com.quadcore.voiceandtext.infrastructure.security.JwtTokenProvider;
import com.quadcore.voiceandtext.presentation.auth.dto.AuthResponse;
import com.quadcore.voiceandtext.presentation.auth.dto.TokenRefreshResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final KakaoOAuthService kakaoOAuthService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository,
                       KakaoOAuthService kakaoOAuthService,
                       JwtTokenProvider jwtTokenProvider,
                       TokenService tokenService) {
        this.userRepository = userRepository;
        this.kakaoOAuthService = kakaoOAuthService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenService = tokenService;
    }

    /**
     * Kakao OAuth 로그인/회원가입
     */
    public AuthResponse kakaoLogin(String kakaoAccessToken) {
        // Kakao에서 사용자 정보 조회
        KakaoUserInfoResponse kakaoUserInfo = kakaoOAuthService.getUserInfo(kakaoAccessToken);

        if (kakaoUserInfo.getKakaoId() == null) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    "카카오에서 사용자 ID를 받을 수 없습니다."
            );
        }

        // 기존 사용자 확인
        User user = userRepository.findByKakaoId(kakaoUserInfo.getKakaoId().toString())
                .orElse(null);

        // 신규 사용자면 회원가입
        if (user == null) {
            user = registerKakaoUser(kakaoUserInfo);
        }

        // 탈퇴된 사용자면 상태 활성화
        if (user.getStatus() == UserStatus.INACTIVE) {
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
            log.info("Reactivated user: {}", user.getId());
        }

        // JWT 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // Refresh Token을 Redis에 저장
        tokenService.saveRefreshToken(
                user.getId(),
                refreshToken,
                jwtTokenProvider.getRefreshTokenExpirationMs()
        );

        log.info("User logged in: {}", user.getId());

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    /**
     * Access Token 재발급
     */
    public TokenRefreshResponse refreshAccessToken(String refreshToken) {
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    "유효하지 않은 Refresh Token입니다."
            );
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // Redis에서 저장된 Refresh Token 확인
        String storedToken = tokenService.getRefreshToken(userId);
        if (!refreshToken.equals(storedToken)) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    "Refresh Token이 일치하지 않습니다."
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        // 새 Access Token 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());

        log.info("Access token refreshed for user: {}", userId);

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(3600)  // 1 hour
                .build();
    }

    /**
     * 로그아웃
     */
    public void logout(Long userId) {
        tokenService.deleteRefreshToken(userId);
        log.info("User logged out: {}", userId);
    }

    /**
     * 회원탈퇴 (Soft delete)
     */
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);

        // 토큰 삭제
        tokenService.deleteAllTokens(userId);

        log.info("User withdrew: {}", userId);
    }

    /**
     * Kakao 사용자 회원가입
     */
    private User registerKakaoUser(KakaoUserInfoResponse kakaoUserInfo) {
        String email = kakaoUserInfo.getEmail();
        if (email == null) {
            // Kakao에서 이메일을 제공하지 않는 경우
            email = "kakao_" + kakaoUserInfo.getKakaoId() + "@kakao.local";
        }

        // 중복 이메일 확인
        userRepository.findByEmail(email).ifPresent(existingUser -> {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    "이미 가입된 이메일입니다."
            );
        });

        User newUser = User.builder()
                .email(email)
                .password(null)  // Kakao 로그인은 비밀번호 없음
                .name(kakaoUserInfo.getNickname() != null ? kakaoUserInfo.getNickname() : "카카오 사용자")
                .kakaoId(kakaoUserInfo.getKakaoId().toString())
                .kakaoProfileImageUrl(kakaoUserInfo.getProfileImageUrl())
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .membershipType(com.quadcore.voiceandtext.domain.user.MembershipType.FREE)
                .build();

        User savedUser = userRepository.save(newUser);
        log.info("New user registered via Kakao: {}", savedUser.getId());

        return savedUser;
    }
}

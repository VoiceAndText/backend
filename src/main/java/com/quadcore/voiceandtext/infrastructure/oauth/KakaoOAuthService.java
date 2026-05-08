package com.quadcore.voiceandtext.infrastructure.oauth;

import com.quadcore.voiceandtext.common.exception.BusinessException;
import com.quadcore.voiceandtext.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class KakaoOAuthService {

    private final String kakaoUserInfoUri;
    private final RestTemplate restTemplate;

    public KakaoOAuthService(
            @Value("${kakao.oauth.user-info-uri}") String kakaoUserInfoUri,
            RestTemplate restTemplate) {
        this.kakaoUserInfoUri = kakaoUserInfoUri;
        this.restTemplate = restTemplate;
    }

    /**
     * Kakao access token을 사용하여 사용자 정보 조회
     */
    public KakaoUserInfoResponse getUserInfo(String kakaoAccessToken) {
        // 입력 검증: 빠른 실패 (Fail-fast)
        if (kakaoAccessToken == null || kakaoAccessToken.isBlank()) {
            log.warn("Invalid Kakao access token: token is null or blank");
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    "카카오 액세스 토큰이 유효하지 않습니다."
            );
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + kakaoAccessToken);
            headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            org.springframework.http.ResponseEntity<KakaoUserInfoResponse> response = restTemplate.exchange(
                    kakaoUserInfoUri,
                    org.springframework.http.HttpMethod.GET,
                    entity,
                    KakaoUserInfoResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }

            throw new BusinessException(
                    ErrorCode.UNKNOWN_ERROR,
                    "카카오 사용자 정보 조회에 실패했습니다."
            );
        } catch (RestClientException ex) {
            log.error("Failed to get user info from Kakao: {}", ex.getMessage());
            throw new BusinessException(
                    ErrorCode.UNKNOWN_ERROR,
                    "카카오 사용자 정보 조회 중 오류가 발생했습니다."
            );
        }
    }
}

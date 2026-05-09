package com.quadcore.voiceandtext.infrastructure.oauth;

import com.quadcore.voiceandtext.common.exception.BusinessException;
import com.quadcore.voiceandtext.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import com.quadcore.voiceandtext.application.oauth.KakaoUserInfoPort;

@Slf4j
@Service
public class KakaoOAuthService implements KakaoUserInfoPort {

    private final String kakaoClientId;
    private final String kakaoRedirectUri;
    private final String kakaoTokenUri;
    private final String kakaoUserInfoUri;
    private final RestTemplate restTemplate;

    public KakaoOAuthService(
            @Value("${kakao.client-id}") String kakaoClientId,
            @Value("${kakao.redirect-uri}") String kakaoRedirectUri,
            @Value("${kakao.token-uri}") String kakaoTokenUri,
            @Value("${kakao.user-info-uri}") String kakaoUserInfoUri,
            RestTemplate restTemplate) {
        this.kakaoClientId = kakaoClientId;
        this.kakaoRedirectUri = kakaoRedirectUri;
        this.kakaoTokenUri = kakaoTokenUri;
        this.kakaoUserInfoUri = kakaoUserInfoUri;
        this.restTemplate = restTemplate;
    }

    /**
     * 카카오 인가 코드를 사용하여 액세스 토큰 발급
     */
    public String getAccessToken(String authorizationCode) {
        // 입력 검증
        if (authorizationCode == null || authorizationCode.isBlank()) {
            log.warn("Invalid authorization code: code is null or blank");
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    "카카오 인가 코드가 유효하지 않습니다."
            );
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", kakaoClientId);
            params.add("redirect_uri", kakaoRedirectUri);
            params.add("code", authorizationCode);

            HttpEntity<MultiValueMap<String, String>> entity =
                new HttpEntity<>(params, headers);

            ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(
                    kakaoTokenUri,
                    HttpMethod.POST,
                    entity,
                    KakaoTokenResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String accessToken = response.getBody().getAccess_token();
                if (accessToken == null || accessToken.isBlank()) {
                    throw new BusinessException(
                            ErrorCode.UNKNOWN_ERROR,
                            "카카오에서 액세스 토큰을 받을 수 없습니다."
                    );
                }
                return accessToken;
            }

            throw new BusinessException(
                    ErrorCode.UNKNOWN_ERROR,
                    "카카오 토큰 발급에 실패했습니다."
            );
        } catch (RestClientResponseException ex) {
            HttpStatusCode status = ex.getStatusCode();
            log.error("Failed to get access token from Kakao: status={}, statusText={}, responseBody={}",
                    status.value(), status.getClass(), ex.getResponseBodyAsString(), ex);

            if (status.is4xxClientError()) {
                throw new BusinessException(
                        ErrorCode.INVALID_REQUEST,
                        "카카오 인가 코드가 잘못되었거나 토큰 요청이 유효하지 않습니다."
                );
            }

            throw new BusinessException(
                    ErrorCode.UNKNOWN_ERROR,
                    "카카오 토큰 발급 중 오류가 발생했습니다."
            );
        } catch (RestClientException ex) {
            log.error("Failed to get access token from Kakao: {}", ex.getMessage(), ex);
            throw new BusinessException(
                    ErrorCode.UNKNOWN_ERROR,
                    "카카오 토큰 발급 중 오류가 발생했습니다."
            );
        }
    }

    /**
     * 카카오 인가 코드를 사용하여 로그인 처리 (토큰 발급 + 사용자 정보 조회)
     */
    public KakaoUserInfoResponse getUserInfoByCode(String authorizationCode) {
        String accessToken = getAccessToken(authorizationCode);
        return getUserInfo(accessToken);
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

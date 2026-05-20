package com.quadcore.voiceandtext.infrastructure.analysis;

import com.quadcore.voiceandtext.domain.analysis.AnalysisRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplateBuilder restTemplateBuilder;
    private RestTemplate restTemplate;
    private final S3Service s3Service;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    @Value("${ai.server.timeout}")
    private int timeout;

    @PostConstruct
    private void init() {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(timeout))
                .setReadTimeout(Duration.ofMillis(timeout))
                .build();
    }

    public void requestAnalysis(AnalysisRequest analysisRequest) {
        String key = null;
        if (analysisRequest.getAudioFile() != null) {
            key = analysisRequest.getAudioFile().getStorageLocation();
            if (key == null || key.isBlank()) {
                key = extractKeyFromFileUrl(analysisRequest.getAudioFile().getFileUrl());
            }
        }

        if (key == null || key.isBlank()) {
            throw new RuntimeException("AudioFile storage location or S3 key is missing for analysisRequest=" + analysisRequest.getId());
        }

        String presignedUrl = s3Service.generatePresignedUrl(key, Duration.ofMinutes(10));
        boolean hasSignature = presignedUrl.contains("X-Amz-Signature");

        log.info("AI 서버 요청 URL 생성: analysisRequestId={}, presignedUrl.length={}, hasSignature={}",
                analysisRequest.getId(), presignedUrl.length(), hasSignature);

        Map<String, Object> requestBody = Map.of(
                "file_url", presignedUrl
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    URI.create(aiServerUrl),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                log.error("AI 서버 요청 실패: analysisRequestId={}, status={}, body={}",
                        analysisRequest.getId(), response.getStatusCodeValue(), response.getBody());
                throw new RuntimeException("AI 서버 요청 실패: " + response.getStatusCodeValue());
            }

            log.info("AI 서버 요청 성공: {}", analysisRequest.getId());
        } catch (HttpStatusCodeException e) {
            log.error("AI 서버 요청 실패: analysisRequestId={}, status={}, responseBody={}",
                    analysisRequest.getId(), e.getRawStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("AI 서버 요청 실패: " + e.getRawStatusCode(), e);
        } catch (Exception e) {
            log.error("AI 서버 요청 중 예외 발생: analysisRequestId={}, message={}",
                    analysisRequest.getId(), e.getMessage(), e);
            throw new RuntimeException("AI 서버 요청 실패", e);
        }
    }

    private String extractKeyFromFileUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return null;
        }

        try {
            URI uri = URI.create(fileUrl);
            String path = uri.getPath();
            if (path == null || path.isBlank()) {
                return null;
            }
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            String host = uri.getHost();
            if (host != null && host.contains("s3")) {
                if (path.contains("/")) {
                    return path.startsWith("/") ? path.substring(1) : path;
                }
                return path;
            }

            return path;
        } catch (Exception e) {
            log.warn("fileUrl에서 S3 키 추출 실패: {}", fileUrl);
            return null;
        }
    }
}
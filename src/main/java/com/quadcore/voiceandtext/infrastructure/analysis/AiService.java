package com.quadcore.voiceandtext.infrastructure.analysis;

import com.quadcore.voiceandtext.domain.analysis.AnalysisRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate;
    private final S3Service s3Service;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    @Value("${ai.server.timeout}")
    private int timeout;

    public void requestAnalysis(AnalysisRequest analysisRequest) {
        String presignedUrl = s3Service.generatePresignedUrl(analysisRequest.getAudioFile().getStorageLocation(), Duration.ofMinutes(10));

        Map<String, Object> requestBody = Map.of(
                "analysisRequestId", analysisRequest.getId(),
                "audioUrl", presignedUrl,
                "durationSeconds", analysisRequest.getAudioFile().getDurationSeconds()
                // callbackUrl은 나중에 추가
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                aiServerUrl + "/api/analyze",
                HttpMethod.POST,
                entity,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("AI 서버 요청 실패: " + response.getStatusCode());
        }

        log.info("AI 서버 요청 성공: {}", analysisRequest.getId());
    }
}
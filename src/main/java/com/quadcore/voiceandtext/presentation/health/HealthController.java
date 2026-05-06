package com.quadcore.voiceandtext.presentation.health;

import com.quadcore.voiceandtext.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Map<String, String>>> root() {
        return ResponseEntity.ok(ApiResponse.success(Map.of("status", "OK")));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        return ResponseEntity.ok(ApiResponse.success(Map.of("status", "OK")));
    }
}

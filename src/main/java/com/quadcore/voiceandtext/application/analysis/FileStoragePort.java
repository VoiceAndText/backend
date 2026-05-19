package com.quadcore.voiceandtext.application.analysis;

import org.springframework.web.multipart.MultipartFile;
import java.time.Duration;

public interface FileStoragePort {
    String uploadFile(MultipartFile file, String key);
    String generatePresignedUrl(String key, Duration expiration);
    void deleteFile(String key);
}

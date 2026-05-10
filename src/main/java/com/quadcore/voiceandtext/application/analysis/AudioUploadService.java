package com.quadcore.voiceandtext.application.analysis;

import com.quadcore.voiceandtext.common.exception.BusinessException;
import com.quadcore.voiceandtext.common.exception.ErrorCode;
import com.quadcore.voiceandtext.domain.analysis.AnalysisRequest;
import com.quadcore.voiceandtext.domain.file.AudioFile;
import com.quadcore.voiceandtext.domain.file.FileSourceType;
import com.quadcore.voiceandtext.domain.file.FileType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AudioUploadService {

    private final S3Service s3Service;

    public AudioFile uploadAudioFile(MultipartFile audio, AnalysisRequest analysisRequest, FileSourceType sourceType, Integer durationSeconds) {
        String originalFileName = audio.getOriginalFilename();
        String storedFileName = generateStoredFileName(analysisRequest, originalFileName);
        String s3Key = generateS3Key(analysisRequest, storedFileName);

        // S3 업로드
        String fileUrl = s3Service.uploadFile(audio, s3Key);

        // AudioFile 엔티티 생성
        return AudioFile.builder()
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .fileUrl(fileUrl)
                .fileSizeBytes(audio.getSize())
                .fileType(FileType.AUDIO)
                .sourceType(sourceType)
                .mimeType(audio.getContentType())
                .durationSeconds(durationSeconds)
                .storageLocation(s3Key)
                .build();
    }

    private String generateStoredFileName(AnalysisRequest analysisRequest, String originalFileName) {
        String extension = getFileExtension(originalFileName);
        return analysisRequest.getId() + "_" + UUID.randomUUID() + (extension != null ? "." + extension : "");
    }

    private String generateS3Key(AnalysisRequest analysisRequest, String storedFileName) {
        if (analysisRequest.getIsGuest()) {
            return "temp/guest/audio/" + analysisRequest.getId() + "/" + storedFileName;
        } else {
            return "members/" + analysisRequest.getUser().getId() + "/audio/" + analysisRequest.getId() + "/" + storedFileName;
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
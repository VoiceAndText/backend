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

    private final FileStoragePort fileStoragePort;

    public AudioFile uploadAudioFile(MultipartFile audio, AnalysisRequest analysisRequest, FileSourceType sourceType, Integer durationSeconds) {
        if (analysisRequest.getId() == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST,
                    "AnalysisRequest id must be assigned before calling uploadAudioFile. Persist the AnalysisRequest first before uploading the audio file.");
        }

        String originalFileName = audio.getOriginalFilename();
        String storedFileName = generateStoredFileName(analysisRequest, originalFileName);
        String s3Key = generateS3Key(analysisRequest, storedFileName);

        // 파일 스토리지 업로드
        String fileUrl = fileStoragePort.uploadFile(audio, s3Key);

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
        if (Boolean.TRUE.equals(analysisRequest.getIsGuest())) {
            return "temp/guest/audio/" + analysisRequest.getId() + "/" + storedFileName;
        }

        if (analysisRequest.getUser() == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST,
                    "Non-guest AnalysisRequest must have a user before generating S3 key.");
        }

        return "members/" + analysisRequest.getUser().getId() + "/audio/" + analysisRequest.getId() + "/" + storedFileName;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
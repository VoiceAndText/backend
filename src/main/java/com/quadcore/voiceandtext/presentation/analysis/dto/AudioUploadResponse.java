package com.quadcore.voiceandtext.presentation.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AudioUploadResponse {
    private Long analysisRequestId;
    private String guestResultToken; // 비회원일 경우에만 포함
}

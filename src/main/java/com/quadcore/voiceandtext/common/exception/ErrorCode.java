package com.quadcore.voiceandtext.common.exception;

public enum ErrorCode {
    UNKNOWN_ERROR("UNKNOWN_ERROR", "알 수 없는 오류가 발생했습니다."),
    INVALID_REQUEST("INVALID_REQUEST", "잘못된 요청입니다."),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),
    UNAUTHORIZED("UNAUTHORIZED", "인증이 필요합니다."),
    FORBIDDEN("FORBIDDEN", "접근이 거부되었습니다."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    INVALID_USER_STATUS("INVALID_USER_STATUS", "유효하지 않은 사용자 상태입니다."),
    CANNOT_CHANGE_OWN_STATUS("CANNOT_CHANGE_OWN_STATUS", "관리자는 자신의 상태를 비활성 또는 정지로 변경할 수 없습니다."),
    INVALID_FILE_SOURCE_TYPE("INVALID_FILE_SOURCE_TYPE", "sourceType은 UPLOAD 또는 RECORD만 가능합니다."),
    AUDIO_FILE_NOT_FOUND("AUDIO_FILE_NOT_FOUND", "음성 파일을 찾을 수 없습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

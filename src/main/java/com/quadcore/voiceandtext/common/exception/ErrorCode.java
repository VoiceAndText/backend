package com.quadcore.voiceandtext.common.exception;

public enum ErrorCode {
    UNKNOWN_ERROR("UNKNOWN_ERROR", "알 수 없는 오류가 발생했습니다."),
    INVALID_REQUEST("INVALID_REQUEST", "잘못된 요청입니다."),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),
    UNAUTHORIZED("UNAUTHORIZED", "인증이 필요합니다."),
    FORBIDDEN("FORBIDDEN", "접근이 거부되었습니다."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    INVALID_FILE_SOURCE_TYPE("INVALID_FILE_SOURCE_TYPE", "sourceType은 UPLOAD 또는 RECORD만 가능합니다.");

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

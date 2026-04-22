package com.quadcore.voiceandtext.common.response;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    // Private constructor to enforce factory methods
    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Success response
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    // Error response
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}

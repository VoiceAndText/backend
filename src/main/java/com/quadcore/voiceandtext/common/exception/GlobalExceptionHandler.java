package com.quadcore.voiceandtext.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getErrorCode().getCode(),
            ex.getMessage(),
            ex.getCause() != null ? ex.getCause().getMessage() : null
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
            ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

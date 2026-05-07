package com.quadcore.voiceandtext.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadcore.voiceandtext.common.exception.ErrorCode;
import com.quadcore.voiceandtext.common.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.error("Authentication failed", authException);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.UNAUTHORIZED.getCode(),
                "인증이 필요합니다.",
                "유효한 인증 정보가 없습니다."
        );

        response.getOutputStream().println(objectMapper.writeValueAsString(errorResponse));
    }
}

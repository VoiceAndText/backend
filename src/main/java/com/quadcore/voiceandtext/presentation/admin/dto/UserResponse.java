package com.quadcore.voiceandtext.presentation.admin.dto;

import com.quadcore.voiceandtext.domain.user.User;
import com.quadcore.voiceandtext.domain.user.UserRole;
import com.quadcore.voiceandtext.domain.user.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long userId;
    private String email;
    private String name;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

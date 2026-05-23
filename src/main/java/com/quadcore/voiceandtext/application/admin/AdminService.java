package com.quadcore.voiceandtext.application.admin;

import com.quadcore.voiceandtext.application.analysis.AnalysisRequestRepository;
import com.quadcore.voiceandtext.application.auth.UserRepository;
import com.quadcore.voiceandtext.common.exception.BusinessException;
import com.quadcore.voiceandtext.common.exception.ErrorCode;
import com.quadcore.voiceandtext.domain.analysis.AnalysisRequest;
import com.quadcore.voiceandtext.domain.user.User;
import com.quadcore.voiceandtext.domain.user.UserRole;
import com.quadcore.voiceandtext.domain.user.UserStatus;
import com.quadcore.voiceandtext.presentation.admin.dto.AnalysisLogResponse;
import com.quadcore.voiceandtext.presentation.admin.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class AdminService {

    private final UserRepository userRepository;
    private final AnalysisRequestRepository analysisRequestRepository;

    public AdminService(UserRepository userRepository, AnalysisRequestRepository analysisRequestRepository) {
        this.userRepository = userRepository;
        this.analysisRequestRepository = analysisRequestRepository;
    }

    private User requireAdmin(Long currentUserId) {
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증 정보가 없습니다.");
        }
        User me = userRepository.findById(currentUserId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (me.getRole() != UserRole.ADMIN) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return me;
    }

    public Page<UserResponse> getAllUsers(Long currentUserId, Pageable pageable) {
        requireAdmin(currentUserId);
        return userRepository.findAll(pageable).map(UserResponse::from);
    }

    public Page<AnalysisLogResponse> getAllLogs(Long currentUserId, Pageable pageable) {
        requireAdmin(currentUserId);
        return analysisRequestRepository.findAll(pageable).map(AnalysisLogResponse::from);
    }

    public Page<AnalysisLogResponse> getUserLogs(Long currentUserId, Long userId, Pageable pageable) {
        requireAdmin(currentUserId);
        userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return analysisRequestRepository.findByUserId(userId, pageable).map(AnalysisLogResponse::from);
    }

    public void updateUserStatus(Long currentUserId, Long userId, String statusStr) {
        User me = requireAdmin(currentUserId);
        User target = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        UserStatus newStatus;
        if (statusStr == null || statusStr.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_USER_STATUS);
        }
        try {
            newStatus = UserStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_USER_STATUS);
        }

        if (me.getId().equals(target.getId()) && newStatus != UserStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.CANNOT_CHANGE_OWN_STATUS);
        }

        target.setStatus(newStatus);
        userRepository.save(target);
    }
}

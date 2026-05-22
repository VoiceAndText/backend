package com.quadcore.voiceandtext.presentation.admin;

import com.quadcore.voiceandtext.application.admin.AdminService;
import com.quadcore.voiceandtext.common.exception.BusinessException;
import com.quadcore.voiceandtext.common.exception.ErrorCode;
import com.quadcore.voiceandtext.common.response.ApiResponse;
import com.quadcore.voiceandtext.presentation.admin.dto.AnalysisLogResponse;
import com.quadcore.voiceandtext.presentation.admin.dto.UpdateUserStatusRequest;
import com.quadcore.voiceandtext.presentation.admin.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "관리자 전용 API")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    @Operation(summary = "전체 사용자 조회", description = "관리자만 접근 가능")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(@AuthenticationPrincipal Long userId) {
        if (userId == null) throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증 정보가 없습니다.");
        List<UserResponse> users = adminService.getAllUsers(userId);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/logs")
    @Operation(summary = "전체 로그 조회", description = "관리자만 접근 가능")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<List<AnalysisLogResponse>>> getAllLogs(@AuthenticationPrincipal Long userId) {
        if (userId == null) throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증 정보가 없습니다.");
        List<AnalysisLogResponse> logs = adminService.getAllLogs(userId);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/users/{userId}/logs")
    @Operation(summary = "사용자별 로그 조회", description = "관리자만 접근 가능")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<List<AnalysisLogResponse>>> getUserLogs(@AuthenticationPrincipal Long userId, @PathVariable("userId") Long userIdPath) {
        if (userId == null) throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증 정보가 없습니다.");
        List<AnalysisLogResponse> logs = adminService.getUserLogs(userId, userIdPath);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @PatchMapping("/users/{userId}/status")
    @Operation(summary = "사용자 상태 변경", description = "관리자만 접근 가능")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(@AuthenticationPrincipal Long userId, @PathVariable("userId") Long userIdPath, @RequestBody UpdateUserStatusRequest request) {
        if (userId == null) throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증 정보가 없습니다.");
        adminService.updateUserStatus(userId, userIdPath, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("사용자 상태가 변경되었습니다."));
    }
}

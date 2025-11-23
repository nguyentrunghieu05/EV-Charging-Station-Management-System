package ut.edu.evcs.project_java.web.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ut.edu.evcs.project_java.domain.user.User;
import ut.edu.evcs.project_java.service.AdminManagementService;
import ut.edu.evcs.project_java.web.dto.admin.ChargingPointDetailDTO;
import ut.edu.evcs.project_java.web.dto.admin.CreateSubscriptionRequest;
import ut.edu.evcs.project_java.web.dto.admin.CreateUserRequest;
import ut.edu.evcs.project_java.web.dto.admin.StationDetailDTO;
import ut.edu.evcs.project_java.web.dto.admin.SubscriptionPlanDTO;
import ut.edu.evcs.project_java.web.dto.admin.UpdateUserRequest;
import ut.edu.evcs.project_java.web.dto.admin.UserDetailDTO;
import ut.edu.evcs.project_java.web.dto.admin.UserSubscriptionDTO;

@Tag(name = "Admin Management", description = "APIs quản lý người dùng, trạm sạc và gói dịch vụ")
@RestController
@RequestMapping("/api/admin/management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminManagementController {

    private final AdminManagementService adminManagementService;

    public AdminManagementController(AdminManagementService adminManagementService) {
        this.adminManagementService = adminManagementService;
    }

    // ============ User Management APIs ============

    @Operation(summary = "Lấy danh sách tất cả người dùng")
    @GetMapping("/users")
    public ResponseEntity<List<UserDetailDTO>> getAllUsers() {
        return ResponseEntity.ok(adminManagementService.getAllUsers());
    }

    @Operation(summary = "Lấy thông tin chi tiết người dùng")
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDetailDTO> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(adminManagementService.getUserById(userId));
    }

    @Operation(summary = "Tạo người dùng mới")
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(adminManagementService.createUser(request));
    }

    @Operation(summary = "Cập nhật thông tin người dùng")
    @PutMapping("/users/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable String userId,
            @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(adminManagementService.updateUser(userId, request));
    }

    @Operation(summary = "Xóa người dùng")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        adminManagementService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lọc người dùng theo loại")
    @GetMapping("/users/by-type/{userType}")
    public ResponseEntity<List<UserDetailDTO>> getUsersByType(@PathVariable String userType) {
        return ResponseEntity.ok(adminManagementService.getUsersByType(userType));
    }

    // ============ Station & Charging Point Management APIs ============

    @Operation(summary = "Lấy danh sách tất cả trạm sạc với thông tin chi tiết")
    @GetMapping("/stations")
    public ResponseEntity<List<StationDetailDTO>> getAllStations() {
        return ResponseEntity.ok(adminManagementService.getAllStationsWithDetails());
    }

    @Operation(summary = "Lấy thông tin chi tiết trạm sạc")
    @GetMapping("/stations/{stationId}")
    public ResponseEntity<StationDetailDTO> getStationDetail(@PathVariable String stationId) {
        return ResponseEntity.ok(adminManagementService.getStationDetail(stationId));
    }

    @Operation(summary = "Lấy danh sách điểm sạc của trạm")
    @GetMapping("/stations/{stationId}/charging-points")
    public ResponseEntity<List<ChargingPointDetailDTO>> getChargingPoints(@PathVariable String stationId) {
        return ResponseEntity.ok(adminManagementService.getChargingPointsByStation(stationId));
    }

    @Operation(summary = "Điều khiển điểm sạc (bật/tắt)")
    @PostMapping("/charging-points/{pointId}/toggle")
    public ResponseEntity<Void> toggleChargingPoint(
            @PathVariable String pointId,
            @RequestParam boolean online) {
        adminManagementService.toggleChargingPoint(pointId, online);
        return ResponseEntity.ok().build();
    }

    // ============ Subscription Plan Management APIs ============

    @Operation(summary = "Lấy danh sách tất cả gói dịch vụ")
    @GetMapping("/subscription-plans")
    public ResponseEntity<List<SubscriptionPlanDTO>> getAllSubscriptionPlans() {
        return ResponseEntity.ok(adminManagementService.getAllSubscriptionPlans());
    }

    @Operation(summary = "Tạo gói dịch vụ mới")
    @PostMapping("/subscription-plans")
    public ResponseEntity<SubscriptionPlanDTO> createSubscriptionPlan(
            @Valid @RequestBody SubscriptionPlanDTO planDTO) {
        return ResponseEntity.ok(adminManagementService.createSubscriptionPlan(planDTO));
    }

    @Operation(summary = "Cập nhật gói dịch vụ")
    @PutMapping("/subscription-plans/{planId}")
    public ResponseEntity<SubscriptionPlanDTO> updateSubscriptionPlan(
            @PathVariable String planId,
            @RequestBody SubscriptionPlanDTO planDTO) {
        return ResponseEntity.ok(adminManagementService.updateSubscriptionPlan(planId, planDTO));
    }

    @Operation(summary = "Xóa gói dịch vụ")
    @DeleteMapping("/subscription-plans/{planId}")
    public ResponseEntity<Void> deleteSubscriptionPlan(@PathVariable String planId) {
        adminManagementService.deleteSubscriptionPlan(planId);
        return ResponseEntity.noContent().build();
    }

    // ============ User Subscription Management APIs ============

    @Operation(summary = "Gán gói dịch vụ cho người dùng")
    @PostMapping("/users/{userId}/subscriptions")
    public ResponseEntity<UserSubscriptionDTO> assignSubscription(
            @PathVariable String userId,
            @Valid @RequestBody CreateSubscriptionRequest request) {
        return ResponseEntity.ok(adminManagementService.assignSubscriptionToUser(userId, request));
    }

    @Operation(summary = "Lấy danh sách gói đăng ký của người dùng")
    @GetMapping("/users/{userId}/subscriptions")
    public ResponseEntity<List<UserSubscriptionDTO>> getUserSubscriptions(@PathVariable String userId) {
        return ResponseEntity.ok(adminManagementService.getUserSubscriptions(userId));
    }

    @Operation(summary = "Hủy gói đăng ký")
    @PostMapping("/subscriptions/{subscriptionId}/cancel")
    public ResponseEntity<Void> cancelSubscription(@PathVariable String subscriptionId) {
        adminManagementService.cancelUserSubscription(subscriptionId);
        return ResponseEntity.ok().build();
    }
}

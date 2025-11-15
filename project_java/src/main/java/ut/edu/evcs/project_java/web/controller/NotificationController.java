package ut.edu.evcs.project_java.web.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ut.edu.evcs.project_java.domain.notification.Notification;
import ut.edu.evcs.project_java.service.CurrentUserService;
import ut.edu.evcs.project_java.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "In-app notifications")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("isAuthenticated()")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    public NotificationController(NotificationService notificationService, CurrentUserService currentUserService) {
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
    }

    private String getCurrentUserId() {
        return currentUserService.getCurrentUserId();
    }

    @Operation(summary = "Danh sách notification của user hiện tại")
    @GetMapping
    public Page<Notification> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        String userId = getCurrentUserId();
        return notificationService.getUserNotifications(userId, PageRequest.of(page, size));
    }

    @Operation(summary = "Đếm số notification chưa đọc")
    @GetMapping("/unread-count")
    public long unreadCount() {
        String userId = getCurrentUserId();
        return notificationService.countUnread(userId);
    }

    @Operation(summary = "Đánh dấu 1 notification là đã đọc")
    @PostMapping("/{id}/read")
    public void markRead(@PathVariable Long id) {
        String userId = getCurrentUserId();
        notificationService.markAsRead(id, userId);
    }

    @Operation(summary = "Đánh dấu tất cả notifications là đã đọc")
    @PostMapping("/read-all")
    public void markAllRead() {
        String userId = getCurrentUserId();
        notificationService.markAllAsRead(userId);
    }
}

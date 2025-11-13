package ut.edu.evcs.project_java.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ut.edu.evcs.project_java.domain.notification.Notification;
import ut.edu.evcs.project_java.domain.notification.NotificationChannel;
import ut.edu.evcs.project_java.domain.notification.NotificationType;
import ut.edu.evcs.project_java.domain.user.User;
import ut.edu.evcs.project_java.repo.NotificationRepository;
import ut.edu.evcs.project_java.repo.UserRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailNotificationService emailNotificationService;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               EmailNotificationService emailNotificationService,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.emailNotificationService = emailNotificationService;
        this.userRepository = userRepository;
    }

    // ============= IN-APP =============

    @Transactional
    public Notification createInAppNotification(String userId, String title, String message, NotificationType type, String metadataJson) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setChannel(NotificationChannel.IN_APP);
        n.setRead(false);
        n.setCreatedAt(LocalDateTime.now());
        n.setMetadata(metadataJson);
        return notificationRepository.save(n);
    }

    // ============= EMAIL + LOG =============

    @Transactional
    public void sendEmailNotification(String userId, String subject, String content, NotificationType type, String metadataJson) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return;
        }

        emailNotificationService.sendEmail(user.getEmail(), subject, content);

        Notification n = new Notification();
        n.setUserId(userId);
        n.setTitle(subject);
        n.setMessage(content);
        n.setType(type);
        n.setChannel(NotificationChannel.EMAIL);
        n.setRead(true); 
        n.setCreatedAt(LocalDateTime.now());
        n.setMetadata(metadataJson);
        notificationRepository.save(n);
    }

    // ============= QUERY =============

    public Page<Notification> getUserNotifications(String userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public long countUnread(String userId) {
        return notificationRepository.countByUserIdAndReadIsFalse(userId);
    }

    // ============= UPDATE READ FLAGS =============

    @Transactional
    public void markAsRead(Long notificationId, String userId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));

        if (!n.getUserId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }
        if (!n.isRead()) {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
            notificationRepository.save(n);
        }
    }

    @Transactional
    public void markAllAsRead(String userId) {
        notificationRepository.findByUserIdAndReadIsFalse(userId)
                .forEach(n -> {
                    n.setRead(true);
                    n.setReadAt(LocalDateTime.now());
                });
    }
}

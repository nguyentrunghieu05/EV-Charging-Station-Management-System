package ut.edu.evcs.project_java.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ut.edu.evcs.project_java.domain.notification.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    long countByUserIdAndReadIsFalse(String userId);

    List<Notification> findByUserIdAndReadIsFalse(String userId);
}

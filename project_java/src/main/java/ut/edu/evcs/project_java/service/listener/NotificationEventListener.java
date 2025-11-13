package ut.edu.evcs.project_java.service.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import ut.edu.evcs.project_java.domain.notification.NotificationType;
import ut.edu.evcs.project_java.domain.session.ChargingSession;
import ut.edu.evcs.project_java.domain.session.Reservation;
import ut.edu.evcs.project_java.service.NotificationService;
import ut.edu.evcs.project_java.service.event.ReservationCancelledEvent;
import ut.edu.evcs.project_java.service.event.ReservationConfirmedEvent;
import ut.edu.evcs.project_java.service.event.ReservationCreatedEvent;
import ut.edu.evcs.project_java.service.event.SessionStoppedEvent;

@Component
public class NotificationEventListener {

    private final NotificationService notificationService;

    public NotificationEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReservationCreated(ReservationCreatedEvent event) {
        Reservation r = event.getReservation();
        String userId = r.getDriverId();

        String title = "Đặt chỗ sạc thành công";
        String message = "Reservation " + r.getId() + " đã được tạo.";

        notificationService.createInAppNotification(
            userId,
            title,
            message,
            NotificationType.RESERVATION_CREATED,
            null
        );

        notificationService.sendEmailNotification(
            userId,
            title,
            message,
            NotificationType.RESERVATION_CREATED,
            null
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReservationCancelled(ReservationCancelledEvent event) {
        Reservation r = event.getReservation();
        String userId = r.getDriverId();

        String title = "Đặt chỗ đã huỷ";
        String message = "Reservation " + r.getId() + " đã được huỷ.";

        notificationService.createInAppNotification(
            userId,
            title,
            message,
            NotificationType.RESERVATION_CANCELLED,
            null
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReservationConfirmed(ReservationConfirmedEvent event) {
        Reservation r = event.getReservation();
        String userId = r.getDriverId();

        String title = "Đặt chỗ đã được xác nhận";
        String message = "Reservation " + r.getId() + " đã được xác nhận.";

        notificationService.createInAppNotification(
            userId,
            title,
            message,
            NotificationType.RESERVATION_CONFIRMED,
            null
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSessionStopped(SessionStoppedEvent event) {
        ChargingSession s = event.getSession();
        String userId = s.getDriverId();

        String title = "Phiên sạc đã kết thúc";
        String message = "Phiên sạc " + s.getId() + " đã dừng. Tổng tiền: " + (s.getTotalCost() != null ? s.getTotalCost() : "N/A");

        notificationService.createInAppNotification(
            userId,
            title,
            message,
            NotificationType.SESSION_STOPPED,
            null
        );
    }
}

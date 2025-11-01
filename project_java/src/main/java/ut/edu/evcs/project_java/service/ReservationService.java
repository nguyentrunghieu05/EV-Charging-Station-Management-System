package ut.edu.evcs.project_java.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ut.edu.evcs.project_java.domain.session.Reservation;
import ut.edu.evcs.project_java.repo.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    private final ReservationRepository repo;

    public ReservationService(ReservationRepository repo) {
        this.repo = repo;
    }

    /**
     * Tạo reservation mới
     * DB trigger sẽ tự động check overlap (trg_reservation_before_ins)
     */
    @Transactional
    public Reservation create(Reservation reservation) {
        if (reservation.getDriverId() == null || reservation.getDriverId().isBlank()) {
            throw new IllegalArgumentException("driverId is required");
        }
        if (reservation.getConnectorId() == null || reservation.getConnectorId().isBlank()) {
            throw new IllegalArgumentException("connectorId is required");
        }
        if (reservation.getStartWindow() == null || reservation.getEndWindow() == null) {
            throw new IllegalArgumentException("startWindow and endWindow are required");
        }
        if (reservation.getStartWindow().isAfter(reservation.getEndWindow()) ||
            reservation.getStartWindow().isEqual(reservation.getEndWindow())) {
            throw new IllegalArgumentException("startWindow must be before endWindow");
        }
        
        // Set status mặc định
        if (reservation.getStatus() == null || reservation.getStatus().isBlank()) {
            reservation.setStatus("PENDING");
        }
        
        try {
            return repo.save(reservation);
        } catch (Exception e) {
            // DB trigger báo lỗi overlap
            if (e.getMessage() != null && e.getMessage().contains("overlaps")) {
                throw new IllegalStateException("Time slot overlaps with existing reservation", e);
            }
            throw e;
        }
    }

    /**
     * Huỷ reservation
     */
    @Transactional
    public void cancel(String id) {
        Reservation r = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + id));
        
        if ("CANCELLED".equals(r.getStatus())) {
            throw new IllegalStateException("Reservation already cancelled");
        }
        
        r.setStatus("CANCELLED");
        repo.save(r);
    }

    /**
     * Confirm reservation
     */
    @Transactional
    public void confirm(String id) {
        Reservation r = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + id));
        
        if ("CANCELLED".equals(r.getStatus())) {
            throw new IllegalStateException("Cannot confirm a cancelled reservation");
        }
        if ("CONFIRMED".equals(r.getStatus())) {
            throw new IllegalStateException("Reservation already confirmed");
        }
        
        r.setStatus("CONFIRMED");
        repo.save(r);
    }

    /**
     * Kiểm tra xem có conflict với reservation hiện tại không
     * (không cần dùng vì DB trigger đã check, nhưng có thể dùng cho UI preview)
     */
    public boolean hasConflict(String connectorId, LocalDateTime start, LocalDateTime end) {
        return repo.findAll().stream()
                .filter(r -> r.getConnectorId().equals(connectorId))
                .filter(r -> !"CANCELLED".equals(r.getStatus()))
                .anyMatch(r -> !(end.isBefore(r.getStartWindow()) || end.isEqual(r.getStartWindow()) ||
                                 start.isAfter(r.getEndWindow()) || start.isEqual(r.getEndWindow())));
    }

    /**
     * Lấy reservation của driver
     */
    public List<Reservation> getByDriverId(String driverId) {
        return repo.findAll().stream()
                .filter(r -> r.getDriverId().equals(driverId))
                .toList();
    }

    /**
     * Lấy reservation active của driver (PENDING hoặc CONFIRMED)
     */
    public List<Reservation> getActiveByDriverId(String driverId) {
        return repo.findAll().stream()
                .filter(r -> r.getDriverId().equals(driverId))
                .filter(r -> "PENDING".equals(r.getStatus()) || "CONFIRMED".equals(r.getStatus()))
                .toList();
    }

    /**
     * Lấy reservation của connector trong khoảng thời gian
     */
    public List<Reservation> getByConnectorAndTimeRange(String connectorId, LocalDateTime start, LocalDateTime end) {
        return repo.findAll().stream()
                .filter(r -> r.getConnectorId().equals(connectorId))
                .filter(r -> !"CANCELLED".equals(r.getStatus()))
                .filter(r -> !(r.getEndWindow().isBefore(start) || r.getEndWindow().isEqual(start) ||
                               r.getStartWindow().isAfter(end) || r.getStartWindow().isEqual(end)))
                .toList();
    }

    /**
     * Lấy chi tiết reservation
     */
    public Optional<Reservation> getById(String id) {
        return repo.findById(id);
    }

    /**
     * Lấy tất cả reservation
     */
    public List<Reservation> getAll() {
        return repo.findAll();
    }
}
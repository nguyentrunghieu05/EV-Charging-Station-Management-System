package ut.edu.evcs.project_java.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ut.edu.evcs.project_java.domain.session.Reservation;
import ut.edu.evcs.project_java.service.ReservationService;
import ut.edu.evcs.project_java.web.dto.reservation.CreateReservationRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Tag(name = "Reservations", description = "Quản lý đặt chỗ sạc")
@RestController
@RequestMapping("/api/reservations")
@SecurityRequirement(name = "bearerAuth")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @Operation(summary = "Tạo reservation mới (DTO)")
    @PostMapping
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public Reservation create(@Valid @RequestBody CreateReservationRequest request) {
        // SỬA: Logic khởi tạo để khớp với Reservation.java mới
        
        // Giả định request.getDriverId() trả về String
        String driverId = request.getDriverId();
        if (driverId == null || driverId.isBlank()) {
             throw new IllegalArgumentException("Invalid driverId format: " + request.getDriverId());
        }
        
        Reservation reservation = Reservation.builder()
                .driverId(driverId) // SỬA: .userId(userId) -> .driverId(driverId)
                .connectorId(request.getConnectorId())
                .startWindow(request.getStartWindow())
                .endWindow(request.getEndWindow())
                .status("PENDING")
                .build();
        
        return service.create(reservation);
    }

    @Operation(summary = "Huỷ reservation")
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public Map<String, String> cancel(@PathVariable String id) { // SỬA: long -> String
        service.cancel(id);
        return Map.of(
            "status", "success",
            "message", "Reservation cancelled",
            "reservationId", id // SỬA: Không cần Long.toString()
        );
    }

    @Operation(summary = "Xác nhận reservation")
    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('CS_STAFF', 'ADMIN')")
    public Map<String, String> confirm(@PathVariable String id) { // SỬA: Long -> String
        service.confirm(id);
        return Map.of(
            "status", "success",
            "message", "Reservation confirmed",
            "reservationId", id // SỬA: Không cần Long.toString()
        );
    }

    @Operation(summary = "Kiểm tra conflict")
    @GetMapping("/check-conflict")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public Map<String, Object> checkConflict(
            @RequestParam String connectorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startWindow,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endWindow) {
        boolean hasConflict = service.hasConflict(connectorId, startWindow, endWindow);
        return Map.of(
            "connectorId", connectorId,
            "startWindow", startWindow,
            "endWindow", endWindow,
            "hasConflict", hasConflict
        );
    }

    @Operation(summary = "Lấy reservation của driver")
    @GetMapping("/driver/{driverId}")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public List<Reservation> getByDriverId(@PathVariable String driverId) {
        return service.getByDriverId(driverId);
    }

    @Operation(summary = "Lấy reservation active của driver")
    @GetMapping("/driver/{driverId}/active")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public List<Reservation> getActiveByDriverId(@PathVariable String driverId) {
        return service.getActiveByDriverId(driverId);
    }

    @Operation(summary = "Lấy reservation theo connector và thời gian")
    @GetMapping("/connector/{connectorId}")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'CS_STAFF', 'ADMIN')")
    public List<Reservation> getByConnectorAndTimeRange(
            @PathVariable String connectorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startWindow,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endWindow) {
        return service.getByConnectorAndTimeRange(connectorId, startWindow, endWindow);
    }

    @Operation(summary = "Lấy chi tiết reservation")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'CS_STAFF', 'ADMIN')")
    public Reservation getById(@PathVariable String id) { // SỬA: Long -> String
        return service.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + id));
    }

    @Operation(summary = "Lấy tất cả reservation")
    @GetMapping
    @PreAuthorize("hasAnyRole('CS_STAFF', 'ADMIN')")
    public List<Reservation> getAll() {
        return service.getAll();
    }

    @Operation(summary = "Lấy reservation theo connector với available slots")
    @GetMapping("/connector/{connectorId}/slots")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'CS_STAFF', 'ADMIN')")
    public Map<String, Object> getAvailableSlots(
            @PathVariable String connectorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        
        // Lấy tất cả reservation của connector trong ngày
        LocalDateTime startOfDay = date.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = date.withHour(23).withMinute(59).withSecond(59);
        
        List<Reservation> bookings = service.getByConnectorAndTimeRange(
            connectorId, startOfDay, endOfDay
        );
        
        return Map.of(
            "connectorId", connectorId,
            "date", date.toLocalDate(),
            "bookings", bookings,
            "totalSlots", 24, // giờ
            "bookedSlots", bookings.size()
        );
    }
}

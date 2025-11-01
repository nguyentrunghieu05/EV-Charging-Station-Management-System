package ut.edu.evcs.project_java.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ut.edu.evcs.project_java.domain.session.Reservation;
import ut.edu.evcs.project_java.service.ReservationService;

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

    @Operation(summary = "Tạo reservation mới")
    @PostMapping
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public Reservation create(@RequestBody Reservation reservation) {
        return service.create(reservation);
    }

    @Operation(summary = "Huỷ reservation")
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public Map<String, String> cancel(@PathVariable String id) {
        service.cancel(id);
        return Map.of(
            "status", "success",
            "message", "Reservation cancelled",
            "reservationId", id
        );
    }

    @Operation(summary = "Xác nhận reservation")
    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('CS_STAFF', 'ADMIN')")
    public Map<String, String> confirm(@PathVariable String id) {
        service.confirm(id);
        return Map.of(
            "status", "success",
            "message", "Reservation confirmed",
            "reservationId", id
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
    public Reservation getById(@PathVariable String id) {
        return service.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + id));
    }

    @Operation(summary = "Lấy tất cả reservation")
    @GetMapping
    @PreAuthorize("hasAnyRole('CS_STAFF', 'ADMIN')")
    public List<Reservation> getAll() {
        return service.getAll();
    }
}
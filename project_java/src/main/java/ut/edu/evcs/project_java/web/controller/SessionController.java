package ut.edu.evcs.project_java.web.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ut.edu.evcs.project_java.domain.session.ChargingSession;
import ut.edu.evcs.project_java.domain.session.Reservation;
import ut.edu.evcs.project_java.service.SessionService;
import ut.edu.evcs.project_java.service.ReservationService;
import ut.edu.evcs.project_java.service.qr.QRCodeService;
import ut.edu.evcs.project_java.service.CurrentUserService;
import ut.edu.evcs.project_java.repo.ChargingSessionRepository;
import ut.edu.evcs.project_java.repo.ConnectorRepository;
import ut.edu.evcs.project_java.repo.TariffPlanRepository;
import ut.edu.evcs.project_java.web.dto.SessionResponse;
import org.springframework.transaction.annotation.Transactional;
import ut.edu.evcs.project_java.domain.station.ChargingPoint;

@RestController
@RequestMapping("/api/sessions")
@PreAuthorize("hasAnyRole('EV_DRIVER', 'CS_STAFF', 'ADMIN')")
public class SessionController {

    private final SessionService sessionService;
    private final ReservationService reservationService;
    private final QRCodeService qrCodeService;
    private final CurrentUserService currentUserService;
    private final ChargingSessionRepository sessionRepo;
    private final ConnectorRepository connectorRepo;
    private final TariffPlanRepository tariffRepo;

    public SessionController(SessionService sessionService,
            ReservationService reservationService,
            QRCodeService qrCodeService,
            CurrentUserService currentUserService,
            ChargingSessionRepository sessionRepo,
            ConnectorRepository connectorRepo,
            TariffPlanRepository tariffRepo) {
        this.sessionService = sessionService;
        this.reservationService = reservationService;
        this.qrCodeService = qrCodeService;
        this.currentUserService = currentUserService;
        this.sessionRepo = sessionRepo;
        this.connectorRepo = connectorRepo;
        this.tariffRepo = tariffRepo;
    }

    @PostMapping("/{id}/stop")
    public ChargingSession stopSession(@PathVariable("id") String id,
            @RequestBody(required = false) Map<String, Object> body) {
        BigDecimal finalKwh = null;
        BigDecimal totalCostOverride = null;
        if (body != null) {
            Object fk = body.get("finalKwh");
            Object te = body.get("totalEnergy");
            Object tc = body.get("totalCost");
            if (fk != null) {
                try {
                    finalKwh = new BigDecimal(fk.toString());
                    if (finalKwh.compareTo(BigDecimal.ZERO) < 0) {
                        throw new IllegalArgumentException("finalKwh must be greater than or equal to 0");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid finalKwh value: " + fk);
                }
            } else if (te != null) {
                try {
                    finalKwh = new BigDecimal(te.toString());
                    if (finalKwh.compareTo(BigDecimal.ZERO) < 0) {
                        throw new IllegalArgumentException("totalEnergy must be greater than or equal to 0");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid totalEnergy value: " + te);
                }
            }
            if (tc != null) {
                try {
                    totalCostOverride = new BigDecimal(tc.toString());
                    if (totalCostOverride.compareTo(BigDecimal.ZERO) < 0) {
                        totalCostOverride = null;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return sessionService.stopSessionWithCost(id, finalKwh, totalCostOverride);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('EV_DRIVER','ADMIN','STAFF')")
    public SessionResponse getById(@PathVariable String id) {
        ChargingSession s = sessionService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + id));

        SessionResponse dto = new SessionResponse();
        dto.setId(s.getId());
        dto.setDriverId(s.getDriverId());
        dto.setVehicleId(s.getVehicleId());
        dto.setConnectorId(s.getConnectorId());
        dto.setReservationId(s.getReservationId());
        dto.setStatus(s.getStatus());
        dto.setStartTime(s.getStartTime());
        dto.setEndTime(s.getEndTime());
        dto.setKwhDelivered(s.getKwhDelivered());
        dto.setEnergyCost(s.getEnergyCost());
        dto.setTimeCost(s.getTimeCost());
        dto.setIdleFee(s.getIdleFee());
        dto.setTotalCost(s.getTotalCost());

        connectorRepo.findById(s.getConnectorId()).ifPresent(conn -> {
            try {
                dto.setConnectorType(conn.getType());
                ChargingPoint point = conn.getChargingPoint();
                if (point != null && point.getStation() != null) {
                    dto.setStationName(point.getStation().getName());
                }
                dto.setMaxCurrentA(conn.getMaxCurrentA());
                dto.setVoltageV(conn.getVoltageV());
                if (conn.getMaxCurrentA() > 0 && conn.getVoltageV() > 0) {
                    dto.setPowerKW(((conn.getMaxCurrentA() * conn.getVoltageV()) / 1000.0));
                }
            } catch (Exception ignored) {
            }
        });

        java.util.Optional<ut.edu.evcs.project_java.domain.tariff.TariffPlan> tariffOpt = s.getTariffId() != null
                ? tariffRepo.findById(s.getTariffId())
                : tariffRepo.findFirstByActiveTrue();
        tariffOpt.ifPresent(t -> dto.setUnitPriceVnd(t.getPricePerKWh()));

        return dto;
    }

    @GetMapping("/me")
    public java.util.List<ChargingSession> getMySessions() {
        String uid = currentUserService.getCurrentUserId();
        return sessionRepo.findByDriverIdOrderByStartTimeDesc(uid);
    }

    @PostMapping("/{id}/update")
    public ChargingSession updateMetrics(@PathVariable String id, @RequestBody Map<String, Object> body) {
        Double energy = null;
        Double cost = null;
        if (body != null) {
            Object e = body.get("energy");
            Object c = body.get("cost");
            if (e != null)
                energy = Double.valueOf(e.toString());
            if (c != null)
                cost = Double.valueOf(c.toString());
        }
        return sessionService.updateMetrics(id, energy, cost);
    }

    @PostMapping("/start-by-connector/{connectorId}")
    public ChargingSession startByConnector(@PathVariable String connectorId,
            @RequestBody(required = false) Map<String, Object> body) {
        String driverId = currentUserService.getCurrentUserId();
        String vehicleId = body != null ? (String) body.getOrDefault("vehicleId", null) : null;
        return sessionService.manualStartSession(driverId, connectorId, vehicleId, null, "qr-start");
    }

    // ===== Start session from reservation (driver flow) =====
    @PostMapping("/start-from-reservation/{reservationId}")
    public ChargingSession startFromReservation(@PathVariable String reservationId,
            @RequestBody(required = false) Map<String, Object> body) {
        Reservation r = reservationService.getById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));
        String driverId = r.getDriverId();
        String connectorId = r.getConnectorId();
        String vehicleId = body != null ? (String) body.getOrDefault("vehicleId", null) : null;
        String notes = body != null ? (String) body.getOrDefault("notes", null) : null;
        ChargingSession s = sessionService.manualStartSession(driverId, connectorId, vehicleId, notes,
                "driver-self-start");
        s.setReservationId(reservationId);
        if (r.getStartWindow() != null) {
            s.setStartTime(r.getStartWindow());
            if (r.getEndWindow() != null) {
                s.setEndTime(r.getEndWindow());
            }
            s = sessionRepo.save(s);
        }
        return s;
    }

    // ===== Generate QR for reservation's connector =====
    @GetMapping("/reservation/{reservationId}/qr")
    public Map<String, String> qrForReservation(@PathVariable String reservationId) {
        Reservation r = reservationService.getById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));
        String qr = qrCodeService.generateQR(r.getConnectorId());
        return Map.of("qr", qr, "connectorId", r.getConnectorId());
    }
}

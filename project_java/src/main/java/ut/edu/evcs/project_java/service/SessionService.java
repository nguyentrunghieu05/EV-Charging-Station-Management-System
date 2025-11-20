package ut.edu.evcs.project_java.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ut.edu.evcs.project_java.domain.session.ChargingSession;
import ut.edu.evcs.project_java.domain.tariff.TariffPlan;
import ut.edu.evcs.project_java.repo.ChargingSessionRepository;
import ut.edu.evcs.project_java.repo.TariffPlanRepository;
import ut.edu.evcs.project_java.repo.ReservationRepository;
import ut.edu.evcs.project_java.domain.session.Reservation;
import ut.edu.evcs.project_java.service.event.SessionStoppedEvent;

@Service
public class SessionService {

    private final ChargingSessionRepository sessionRepo;
    private final TariffPlanRepository tariffRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final ReservationRepository reservationRepo;

    private static final String STATUS_STARTED = "STARTED";
    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_STOPPED = "STOPPED";

    public SessionService(ChargingSessionRepository sessionRepo,
            TariffPlanRepository tariffRepo,
            ApplicationEventPublisher eventPublisher,
            ReservationRepository reservationRepo) {
        this.sessionRepo = sessionRepo;
        this.tariffRepo = tariffRepo;
        this.eventPublisher = eventPublisher;
        this.reservationRepo = reservationRepo;
    }

    // ============= STOP SESSION =============
    @Transactional
    public ChargingSession stopSession(String sessionId, BigDecimal finalKwh) {
        ChargingSession s = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        String st = s.getStatus() == null ? "" : s.getStatus().toUpperCase();
        if (!(STATUS_STARTED.equals(st) || STATUS_ACTIVE.equals(st))) {
            throw new RuntimeException("Session not active; current status: " + st);
        }

        BigDecimal delivered;
        // Priority 1: Use meter readings if available
        if (s.getMeterStartKwh() != null && s.getMeterEndKwh() != null) {
            delivered = s.getMeterEndKwh().subtract(s.getMeterStartKwh());
        }
        // Priority 2: Use finalKwh from frontend (charging page calculation)
        else if (finalKwh != null && finalKwh.compareTo(BigDecimal.ZERO) > 0) {
            delivered = finalKwh;
        }
        // Priority 3: Use existing kwhDelivered from session
        else if (s.getKwhDelivered() > 0) {
            delivered = BigDecimal.valueOf(s.getKwhDelivered());
        }
        // Priority 4: Estimate based on time and power (fallback only)
        else {
            double hours = java.time.Duration.between(s.getStartTime(), LocalDateTime.now()).toMinutes() / 60.0;
            double powerKW = 50.0; // Fallback constant for fast charging simulation
            double kwhEst = Math.max(0d, powerKW * hours * 0.7);
            delivered = BigDecimal.valueOf(kwhEst);
        }

        delivered = delivered.max(BigDecimal.ZERO).setScale(3, RoundingMode.HALF_UP);

        s.setEndTime(LocalDateTime.now());
        s.setKwhDelivered(delivered.doubleValue());

        TariffPlan tariff = resolveTariff(s.getTariffId());
        if (tariff != null && tariff.getPricePerKWh() != null) {
            s.setUnitPriceVnd(tariff.getPricePerKWh());
        } else {
            s.setUnitPriceVnd(new BigDecimal("3000"));
        }

        MoneyBreakdown breakdown = calculateCost(s, tariff);

        s.setEnergyCost(breakdown.getEnergy());
        s.setTimeCost(breakdown.getTime());
        s.setIdleFee(breakdown.getIdle());
        s.setTotalCost(breakdown.getTotal());
        s.setStatus(STATUS_STOPPED);

        ChargingSession saved = sessionRepo.save(s);
        eventPublisher.publishEvent(new SessionStoppedEvent(saved));
        return saved;
    }

    @Transactional
    public ChargingSession stopSessionWithCost(String sessionId, BigDecimal finalKwh, BigDecimal totalCostOverride) {
        ChargingSession s = stopSession(sessionId, finalKwh);

        // If frontend provides totalCost override, use it directly
        // The backend has already calculated individual components (energy, time, idle)
        // We trust the frontend's total calculation which includes energy + time + VAT
        if (totalCostOverride != null && totalCostOverride.compareTo(BigDecimal.ZERO) >= 0) {
            BigDecimal currentTotal = s.getTotalCost() != null ? s.getTotalCost() : BigDecimal.ZERO;

            // Only adjust if there's a significant difference or if backend calculated 0
            // but frontend has value
            if (currentTotal.compareTo(totalCostOverride) != 0) {
                s.setTotalCost(totalCostOverride.setScale(2, RoundingMode.HALF_UP));

                // Reconcile components to match the override
                // If backend calculated 0 (or very different), we need to adjust components
                // so the invoice doesn't show Total > 0 but Components = 0

                BigDecimal currentSubtotal = (s.getEnergyCost() != null ? s.getEnergyCost() : BigDecimal.ZERO)
                        .add(s.getTimeCost() != null ? s.getTimeCost() : BigDecimal.ZERO)
                        .add(s.getIdleFee() != null ? s.getIdleFee() : BigDecimal.ZERO);

                // Calculate expected subtotal from override total (Total = Subtotal * 1.1)
                BigDecimal expectedSubtotal = totalCostOverride.divide(BigDecimal.valueOf(1.1), 2,
                        RoundingMode.HALF_UP);

                if (currentSubtotal.compareTo(BigDecimal.ZERO) == 0) {
                    // If backend calculated 0, attribute all to Energy (if kwh > 0) or Time
                    if (s.getKwhDelivered() > 0) {
                        s.setEnergyCost(expectedSubtotal);
                    } else {
                        s.setTimeCost(expectedSubtotal);
                    }
                } else {
                    // If backend has some values, scale them proportionally
                    // Factor = ExpectedSubtotal / CurrentSubtotal
                    BigDecimal factor = expectedSubtotal.divide(currentSubtotal, 4, RoundingMode.HALF_UP);

                    if (s.getEnergyCost() != null)
                        s.setEnergyCost(s.getEnergyCost().multiply(factor).setScale(2, RoundingMode.HALF_UP));
                    if (s.getTimeCost() != null)
                        s.setTimeCost(s.getTimeCost().multiply(factor).setScale(2, RoundingMode.HALF_UP));
                    if (s.getIdleFee() != null)
                        s.setIdleFee(s.getIdleFee().multiply(factor).setScale(2, RoundingMode.HALF_UP));
                }

                sessionRepo.save(s);
            }
        }
        return s;
    }

    // ============= COST CALCULATION =============
    public MoneyBreakdown calculateCost(ChargingSession s, TariffPlan t) {
        BigDecimal pricePerKwh = (t != null && t.getPricePerKWh() != null)
                ? t.getPricePerKWh()
                : BigDecimal.ZERO;

        BigDecimal energy = BigDecimal.valueOf(s.getKwhDelivered())
                .multiply(pricePerKwh)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal time = BigDecimal.ZERO;
        BigDecimal idle = BigDecimal.ZERO;

        if (t != null) {
            BigDecimal ppm = t.getPricePerMinute() == null ? BigDecimal.ZERO : t.getPricePerMinute();
            BigDecimal ipm = t.getIdleFeePerMinute() == null ? BigDecimal.ZERO : t.getIdleFeePerMinute();
            if (ppm.compareTo(BigDecimal.ZERO) > 0 || ipm.compareTo(BigDecimal.ZERO) > 0) {
                java.time.LocalDateTime end = s.getEndTime() != null ? s.getEndTime() : java.time.LocalDateTime.now();
                long minutes = Math.max(0, java.time.Duration.between(s.getStartTime(), end).toMinutes());
                if (ppm.compareTo(BigDecimal.ZERO) > 0) {
                    time = ppm.multiply(BigDecimal.valueOf(minutes)).setScale(2, RoundingMode.HALF_UP);
                }
                if (ipm.compareTo(BigDecimal.ZERO) > 0 && s.getReservationId() != null) {
                    Reservation r = reservationRepo.findById(s.getReservationId()).orElse(null);
                    if (r != null && r.getEndWindow() != null) {
                        long idleMin = Math.max(0, java.time.Duration.between(r.getEndWindow(), end).toMinutes());
                        idle = ipm.multiply(BigDecimal.valueOf(idleMin)).setScale(2, RoundingMode.HALF_UP);
                    }
                }
            }
        }

        BigDecimal subtotal = energy.add(time).add(idle);
        BigDecimal vat = subtotal
                .multiply(BigDecimal.valueOf(10).movePointLeft(2))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(vat).setScale(2, RoundingMode.HALF_UP);

        return new MoneyBreakdown(energy, time, idle, vat, total);
    }

    private TariffPlan resolveTariff(String tariffId) {
        if (tariffId != null) {
            Optional<TariffPlan> t = tariffRepo.findById(tariffId);
            if (t.isPresent())
                return ensureNonZeroPrice(t.get());
        }
        Optional<TariffPlan> active = tariffRepo.findFirstByActiveTrue();
        if (active.isPresent())
            return ensureNonZeroPrice(active.get());
        TariffPlan fallback = new TariffPlan();
        fallback.setPricePerKWh(new BigDecimal("3000"));
        return fallback;
    }

    private TariffPlan ensureNonZeroPrice(TariffPlan t) {
        if (t.getPricePerKWh() == null || t.getPricePerKWh().compareTo(BigDecimal.ZERO) <= 0) {
            t.setPricePerKWh(new BigDecimal("3000"));
        }
        if (t.getPricePerMinute() == null || t.getPricePerMinute().compareTo(BigDecimal.ZERO) <= 0) {
            t.setPricePerMinute(new BigDecimal("150"));
        }
        if (t.getIdleFeePerMinute() == null || t.getIdleFeePerMinute().compareTo(BigDecimal.ZERO) <= 0) {
            t.setIdleFeePerMinute(new BigDecimal("500"));
        }
        return t;
    }

    // ---- MoneyBreakdown inner class ----
    public static class MoneyBreakdown {
        private final BigDecimal energy;
        private final BigDecimal time;
        private final BigDecimal idle;
        private final BigDecimal vat;
        private final BigDecimal total;

        public MoneyBreakdown(BigDecimal energy, BigDecimal time, BigDecimal idle, BigDecimal vat, BigDecimal total) {
            this.energy = energy;
            this.time = time;
            this.idle = idle;
            this.vat = vat;
            this.total = total;
        }

        public BigDecimal getEnergy() {
            return energy;
        }

        public BigDecimal getTime() {
            return time;
        }

        public BigDecimal getIdle() {
            return idle;
        }

        public BigDecimal getVat() {
            return vat;
        }

        public BigDecimal getTotal() {
            return total;
        }
    }

    // ============= MANUAL START (Staff) =============
    @Transactional
    public ChargingSession manualStartSession(
            String driverId,
            String connectorId,
            String vehicleId,
            String notes,
            String staffUsername) {
        TariffPlan activeTariff = resolveTariff(null);

        ChargingSession s = new ChargingSession();
        s.setDriverId(driverId);
        s.setConnectorId(connectorId);
        s.setVehicleId(vehicleId);
        s.setStartTime(LocalDateTime.now());
        s.setStatus(STATUS_STARTED);
        s.setTariffId(activeTariff.getId());

        return sessionRepo.save(s);
    }

    // ============= GET BY ID =============
    public Optional<ChargingSession> getById(String id) {
        return sessionRepo.findById(id);
    }

    // ============= UPDATE METRICS (from frontend simulation) =============
    @Transactional
    public ChargingSession updateMetrics(String id, Double energyKwh, Double totalCostVnd) {
        ChargingSession s = sessionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found: " + id));

        // Don't update metrics if session is already stopped
        String st = s.getStatus() == null ? "" : s.getStatus().toUpperCase();
        if (STATUS_STOPPED.equals(st)) {
            return s; // Return without updating to preserve final values
        }

        if (energyKwh != null) {
            s.setKwhDelivered(Math.max(0d, energyKwh));
        }
        if (totalCostVnd != null) {
            s.setTotalCost(BigDecimal.valueOf(totalCostVnd).setScale(2, RoundingMode.HALF_UP));
        }
        if (!STATUS_ACTIVE.equalsIgnoreCase(s.getStatus())) {
            s.setStatus(STATUS_ACTIVE);
        }
        return sessionRepo.save(s);
    }

    // ============= ACTIVE SESSIONS =============
    public List<ChargingSession> getActiveSessions() {
        return sessionRepo.findByStatusIn(List.of(STATUS_STARTED, STATUS_ACTIVE));
    }

    public List<ChargingSession> getActiveSessionsByStation(String stationId) {
        // Hiện entity chưa có stationId → tạm trả về all active
        return getActiveSessions();
    }

    // ============= OVERRIDE PRICE (Admin) =============
    @Transactional
    public ChargingSession overridePrice(
            String sessionId,
            BigDecimal newPricePerKwh,
            String reason,
            String adminUsername) {
        if (newPricePerKwh == null || newPricePerKwh.signum() <= 0) {
            throw new IllegalArgumentException("newPricePerKwh must be > 0");
        }

        ChargingSession s = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        TariffPlan tmpTariff = new TariffPlan();
        tmpTariff.setPricePerKWh(newPricePerKwh);

        MoneyBreakdown breakdown = calculateCost(s, tmpTariff);

        s.setEnergyCost(breakdown.getEnergy());
        s.setTimeCost(breakdown.getTime());
        s.setIdleFee(breakdown.getIdle());
        s.setTotalCost(breakdown.getTotal());

        return sessionRepo.save(s);
    }
}

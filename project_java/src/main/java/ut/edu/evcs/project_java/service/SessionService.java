package ut.edu.evcs.project_java.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import ut.edu.evcs.project_java.service.event.SessionStoppedEvent;

@Service
public class SessionService {

    private final ChargingSessionRepository sessionRepo;
    private final TariffPlanRepository tariffRepo;
    private final ApplicationEventPublisher eventPublisher;

    private static final String STATUS_STARTED = "STARTED";
    private static final String STATUS_ACTIVE  = "ACTIVE";
    private static final String STATUS_STOPPED = "STOPPED";

    public SessionService(ChargingSessionRepository sessionRepo,
                          TariffPlanRepository tariffRepo,
                          ApplicationEventPublisher eventPublisher) {
        this.sessionRepo = sessionRepo;
        this.tariffRepo = tariffRepo;
        this.eventPublisher = eventPublisher;
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
        if (s.getMeterStartKwh() != null && s.getMeterEndKwh() != null) {
            delivered = s.getMeterEndKwh().subtract(s.getMeterStartKwh());
        } else if (finalKwh != null) {
            delivered = finalKwh;
        } else {
            delivered = BigDecimal.valueOf(s.getKwhDelivered());
        }
        delivered = delivered.max(BigDecimal.ZERO).setScale(3, RoundingMode.HALF_UP);

        s.setEndTime(LocalDateTime.now());
        s.setKwhDelivered(delivered.doubleValue());

        TariffPlan tariff = resolveTariff(s.getTariffId());
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

        BigDecimal subtotal = energy.add(time).add(idle);
        BigDecimal vat = subtotal
                .multiply(BigDecimal.valueOf(8).movePointLeft(2))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(vat).setScale(2, RoundingMode.HALF_UP);

        return new MoneyBreakdown(energy, time, idle, vat, total);
    }

    private TariffPlan resolveTariff(String tariffId) {
        if (tariffId != null) {
            Optional<TariffPlan> t = tariffRepo.findById(tariffId);
            if (t.isPresent()) return t.get();
        }
        return tariffRepo.findFirstByActiveTrue()
                .orElseThrow(() -> new RuntimeException("No active tariff found"));
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
        public BigDecimal getEnergy() { return energy; }
        public BigDecimal getTime() { return time; }
        public BigDecimal getIdle() { return idle; }
        public BigDecimal getVat() { return vat; }
        public BigDecimal getTotal() { return total; }
    }

    // ============= MANUAL START (Staff) =============
    @Transactional
    public ChargingSession manualStartSession(
            String driverId,
            String connectorId,
            String vehicleId,
            String notes,
            String staffUsername
    ) {
        TariffPlan activeTariff = resolveTariff(null);

        ChargingSession s = new ChargingSession();
        s.setDriverId(driverId);
        s.setConnectorId(connectorId);
        s.setVehicleId(vehicleId);
        s.setStatus(STATUS_STARTED);
        s.setTariffId(activeTariff.getId());

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
            String adminUsername
    ) {
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

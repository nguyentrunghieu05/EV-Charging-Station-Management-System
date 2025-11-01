package ut.edu.evcs.project_java.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ut.edu.evcs.project_java.domain.session.ChargingSession;
import ut.edu.evcs.project_java.domain.tariff.TariffPlan;
import ut.edu.evcs.project_java.repo.ChargingSessionRepository;
import ut.edu.evcs.project_java.repo.TariffPlanRepository;

@Service
public class SessionService {

    private final ChargingSessionRepository sessionRepo;
    private final TariffPlanRepository tariffRepo;

    public SessionService(ChargingSessionRepository sessionRepo, TariffPlanRepository tariffRepo) {
        this.sessionRepo = sessionRepo;
        this.tariffRepo = tariffRepo;
    }

    @Transactional
    public ChargingSession stopSession(String sessionId, BigDecimal finalKwh) {
        ChargingSession s = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
        String st = s.getStatus() == null ? "" : s.getStatus().toUpperCase();
        if (!("STARTED".equals(st) || "ACTIVE".equals(st))) {
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
        s.setStatus("STOPPED");

        return sessionRepo.save(s);
    }

    public MoneyBreakdown calculateCost(ChargingSession s, TariffPlan t) {
        BigDecimal energy = BigDecimal.valueOf(s.getKwhDelivered())
                .multiply(t.getPricePerKWh() == null ? BigDecimal.ZERO : t.getPricePerKWh())
                .setScale(2, RoundingMode.HALF_UP);

        // For demo: time/idle fees set to ZERO unless tariff has values and you want to compute them.
        BigDecimal time = BigDecimal.ZERO;
        BigDecimal idle = BigDecimal.ZERO;

        BigDecimal subtotal = energy.add(time).add(idle);
        BigDecimal vat = subtotal.multiply(BigDecimal.valueOf(8).movePointLeft(2)).setScale(2, RoundingMode.HALF_UP);
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
}

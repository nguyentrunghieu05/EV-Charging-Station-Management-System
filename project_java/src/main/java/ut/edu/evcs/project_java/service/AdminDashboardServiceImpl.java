package ut.edu.evcs.project_java.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ut.edu.evcs.project_java.domain.station.Station;
import ut.edu.evcs.project_java.domain.station.StationStatus;
import ut.edu.evcs.project_java.repo.ChargingSessionRepository;
import ut.edu.evcs.project_java.repo.StationRepository;
import ut.edu.evcs.project_java.web.dto.admin.PeakHourDTO;
import ut.edu.evcs.project_java.web.dto.admin.RevenueByDayDTO;
import ut.edu.evcs.project_java.web.dto.admin.RevenueSummaryDTO;
import ut.edu.evcs.project_java.web.dto.admin.UsageStatsDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final ChargingSessionRepository sessionRepo;
    private final StationRepository stationRepo;

    public AdminDashboardServiceImpl(ChargingSessionRepository sessionRepo,
            StationRepository stationRepo) {
        this.sessionRepo = sessionRepo;
        this.stationRepo = stationRepo;
    }

    // Helper method to safely convert any Number to BigDecimal
    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null)
            return BigDecimal.ZERO;
        if (obj instanceof BigDecimal)
            return (BigDecimal) obj;
        if (obj instanceof Number)
            return BigDecimal.valueOf(((Number) obj).doubleValue());
        return BigDecimal.ZERO;
    }

    @Override
    public RevenueSummaryDTO getRevenueSummary(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.atTime(LocalTime.MAX);

        List<Object[]> rows = sessionRepo.revenueByDay(fromDt, toDt);
        List<RevenueByDayDTO> byDay = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal energy = BigDecimal.ZERO;
        BigDecimal time = BigDecimal.ZERO;
        BigDecimal idle = BigDecimal.ZERO;

        for (Object[] r : rows) {
            // Handle potential date casting issues if DB returns Timestamp
            LocalDate day;
            if (r[0] instanceof java.sql.Date) {
                day = ((java.sql.Date) r[0]).toLocalDate();
            } else if (r[0] instanceof java.sql.Timestamp) {
                day = ((java.sql.Timestamp) r[0]).toLocalDateTime().toLocalDate();
            } else {
                day = LocalDate.now(); // Fallback
            }

            BigDecimal dayTotal = toBigDecimal(r[1]);
            BigDecimal dayEnergy = toBigDecimal(r[2]);
            BigDecimal dayTime = toBigDecimal(r[3]);
            BigDecimal dayIdle = toBigDecimal(r[4]);

            byDay.add(new RevenueByDayDTO(day, dayTotal, dayEnergy, dayTime, dayIdle));

            total = total.add(dayTotal);
            energy = energy.add(dayEnergy);
            time = time.add(dayTime);
            idle = idle.add(dayIdle);
        }

        return new RevenueSummaryDTO(
                from,
                to,
                total,
                energy,
                time,
                idle,
                byDay);
    }

    @Override
    public UsageStatsDTO getUsageStats(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.atTime(LocalTime.MAX);

        long totalSessions = sessionRepo.countSessionsBetween(fromDt, toDt);
        long completedSessions = sessionRepo.countCompletedSessionsBetween(fromDt, toDt);
        long activeSessions = sessionRepo.countActiveSessionsBetween(fromDt, toDt);

        // Count active stations (status != OFFLINE)
        long activeChargingPoints = stationRepo.countByStatusNot(StationStatus.OFFLINE);

        BigDecimal totalKwh = sessionRepo.sumKwhBetween(fromDt, toDt);
        if (totalKwh == null)
            totalKwh = BigDecimal.ZERO;

        Double avgDurationMinutes = sessionRepo.avgDurationMinutes(fromDt, toDt);

        Double avgKwhPerSession = null;
        if (completedSessions > 0) {
            avgKwhPerSession = totalKwh
                    .divide(BigDecimal.valueOf(completedSessions), 2, java.math.RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return new UsageStatsDTO(
                totalSessions,
                completedSessions,
                activeSessions,
                totalKwh,
                avgKwhPerSession,
                avgDurationMinutes,
                activeChargingPoints);
    }

    @Override
    public List<PeakHourDTO> getPeakHours(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.atTime(LocalTime.MAX);

        List<Object[]> rows = sessionRepo.peakHours(fromDt, toDt);
        List<PeakHourDTO> result = new ArrayList<>();

        for (Object[] r : rows) {
            Integer hour = r[0] != null ? ((Number) r[0]).intValue() : 0;
            Long count = r[1] != null ? ((Number) r[1]).longValue() : 0L;
            BigDecimal kwh = toBigDecimal(r[2]);
            result.add(new PeakHourDTO(hour, count, kwh));
        }

        return result;
    }

    @Override
    @Transactional
    public void toggleStationStatus(String stationId, StationStatus newStatus) {
        Station station = stationRepo.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("Station not found: " + stationId));
        station.setStatus(newStatus);
        stationRepo.save(station);
    }
}

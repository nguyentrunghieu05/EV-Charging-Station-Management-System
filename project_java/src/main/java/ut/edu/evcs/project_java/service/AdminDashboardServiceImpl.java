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
            LocalDate day = ((java.sql.Date) r[0]).toLocalDate();
            BigDecimal dayTotal = (BigDecimal) r[1];
            BigDecimal dayEnergy = (BigDecimal) r[2];
            BigDecimal dayTime = (BigDecimal) r[3];
            BigDecimal dayIdle = (BigDecimal) r[4];

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
                byDay
        );
    }

    @Override
    public UsageStatsDTO getUsageStats(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.atTime(LocalTime.MAX);

        long totalSessions = sessionRepo.countSessionsBetween(fromDt, toDt);
        long completedSessions = sessionRepo.countCompletedSessionsBetween(fromDt, toDt);
        long activeSessions = sessionRepo.countActiveSessionsBetween(fromDt, toDt);

        BigDecimal totalKwh = sessionRepo.sumKwhBetween(fromDt, toDt);
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
                avgDurationMinutes
        );
    }

    @Override
    public List<PeakHourDTO> getPeakHours(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.atTime(LocalTime.MAX);

        List<Object[]> rows = sessionRepo.peakHours(fromDt, toDt);
        List<PeakHourDTO> result = new ArrayList<>();

        for (Object[] r : rows) {
            Integer hour = (Integer) r[0];
            Long count = (Long) r[1];
            BigDecimal kwh = (BigDecimal) r[2];
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

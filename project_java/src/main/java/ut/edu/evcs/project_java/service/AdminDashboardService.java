package ut.edu.evcs.project_java.service;

import java.time.LocalDate;
import java.util.List;

import ut.edu.evcs.project_java.domain.station.StationStatus;
import ut.edu.evcs.project_java.web.dto.admin.PeakHourDTO;
import ut.edu.evcs.project_java.web.dto.admin.RevenueSummaryDTO;
import ut.edu.evcs.project_java.web.dto.admin.UsageStatsDTO;

public interface AdminDashboardService {

    RevenueSummaryDTO getRevenueSummary(LocalDate from, LocalDate to);

    UsageStatsDTO getUsageStats(LocalDate from, LocalDate to);

    List<PeakHourDTO> getPeakHours(LocalDate from, LocalDate to);

    void toggleStationStatus(String stationId, StationStatus newStatus);
}

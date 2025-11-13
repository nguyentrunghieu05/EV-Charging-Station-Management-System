package ut.edu.evcs.project_java.web.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ut.edu.evcs.project_java.domain.station.StationStatus;
import ut.edu.evcs.project_java.service.AdminDashboardService;
import ut.edu.evcs.project_java.web.dto.admin.PeakHourDTO;
import ut.edu.evcs.project_java.web.dto.admin.RevenueSummaryDTO;
import ut.edu.evcs.project_java.web.dto.admin.UsageStatsDTO;

@Tag(name = "Admin Dashboard", description = "Admin analytics & station management")
@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminDashboardService dashboardService;

    public AdminController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(summary = "Revenue summary cho dashboard")
    @GetMapping("/dashboard/revenue")
    public RevenueSummaryDTO revenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return dashboardService.getRevenueSummary(from, to);
    }

    @Operation(summary = "Usage statistics cho dashboard")
    @GetMapping("/dashboard/usage-stats")
    public UsageStatsDTO usageStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return dashboardService.getUsageStats(from, to);
    }

    @Operation(summary = "Peak hours trong khoảng thời gian")
    @GetMapping("/dashboard/peak-hours")
    public List<PeakHourDTO> peakHours(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return dashboardService.getPeakHours(from, to);
    }

    @Operation(summary = "Toggle trạng thái station (ONLINE/OFFLINE/MAINTENANCE)")
    @PostMapping("/stations/{id}/toggle")
    public Map<String, Object> toggleStation(
            @PathVariable("id") String stationId,
            @RequestParam("status") StationStatus status
    ) {
        dashboardService.toggleStationStatus(stationId, status);
        return Map.of(
                "stationId", stationId,
                "newStatus", status.name()
        );
    }
}


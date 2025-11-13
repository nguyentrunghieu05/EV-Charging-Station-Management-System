package ut.edu.evcs.project_java.web.dto.admin;

import java.math.BigDecimal;

public class UsageStatsDTO {

    private long totalSessions;
    private long completedSessions;
    private long activeSessions;

    private BigDecimal totalKwhDelivered;
    private Double avgKwhPerSession;
    private Double avgSessionDurationMinutes;

    public UsageStatsDTO() {
    }

    public UsageStatsDTO(long totalSessions,
                         long completedSessions,
                         long activeSessions,
                         BigDecimal totalKwhDelivered,
                         Double avgKwhPerSession,
                         Double avgSessionDurationMinutes) {
        this.totalSessions = totalSessions;
        this.completedSessions = completedSessions;
        this.activeSessions = activeSessions;
        this.totalKwhDelivered = totalKwhDelivered;
        this.avgKwhPerSession = avgKwhPerSession;
        this.avgSessionDurationMinutes = avgSessionDurationMinutes;
    }

    public long getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(long totalSessions) {
        this.totalSessions = totalSessions;
    }

    public long getCompletedSessions() {
        return completedSessions;
    }

    public void setCompletedSessions(long completedSessions) {
        this.completedSessions = completedSessions;
    }

    public long getActiveSessions() {
        return activeSessions;
    }

    public void setActiveSessions(long activeSessions) {
        this.activeSessions = activeSessions;
    }

    public BigDecimal getTotalKwhDelivered() {
        return totalKwhDelivered;
    }

    public void setTotalKwhDelivered(BigDecimal totalKwhDelivered) {
        this.totalKwhDelivered = totalKwhDelivered;
    }

    public Double getAvgKwhPerSession() {
        return avgKwhPerSession;
    }

    public void setAvgKwhPerSession(Double avgKwhPerSession) {
        this.avgKwhPerSession = avgKwhPerSession;
    }

    public Double getAvgSessionDurationMinutes() {
        return avgSessionDurationMinutes;
    }

    public void setAvgSessionDurationMinutes(Double avgSessionDurationMinutes) {
        this.avgSessionDurationMinutes = avgSessionDurationMinutes;
    }
}

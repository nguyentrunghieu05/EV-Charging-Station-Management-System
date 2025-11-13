package ut.edu.evcs.project_java.web.dto.admin;

import java.math.BigDecimal;

public class PeakHourDTO {

    private int hourOfDay;
    private long sessionsCount;
    private BigDecimal totalKwh;

    public PeakHourDTO() {
    }

    public PeakHourDTO(int hourOfDay, long sessionsCount, BigDecimal totalKwh) {
        this.hourOfDay = hourOfDay;
        this.sessionsCount = sessionsCount;
        this.totalKwh = totalKwh;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public long getSessionsCount() {
        return sessionsCount;
    }

    public void setSessionsCount(long sessionsCount) {
        this.sessionsCount = sessionsCount;
    }

    public BigDecimal getTotalKwh() {
        return totalKwh;
    }

    public void setTotalKwh(BigDecimal totalKwh) {
        this.totalKwh = totalKwh;
    }
}

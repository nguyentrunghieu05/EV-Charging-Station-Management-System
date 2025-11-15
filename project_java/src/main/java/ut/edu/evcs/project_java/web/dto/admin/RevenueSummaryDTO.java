package ut.edu.evcs.project_java.web.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RevenueSummaryDTO {

    private LocalDate fromDate;
    private LocalDate toDate;

    private BigDecimal totalRevenue;
    private BigDecimal energyRevenue;
    private BigDecimal timeRevenue;
    private BigDecimal idleFeeRevenue;

    private List<RevenueByDayDTO> byDay;

    public RevenueSummaryDTO() {
    }

    public RevenueSummaryDTO(LocalDate fromDate,
                             LocalDate toDate,
                             BigDecimal totalRevenue,
                             BigDecimal energyRevenue,
                             BigDecimal timeRevenue,
                             BigDecimal idleFeeRevenue,
                             List<RevenueByDayDTO> byDay) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.totalRevenue = totalRevenue;
        this.energyRevenue = energyRevenue;
        this.timeRevenue = timeRevenue;
        this.idleFeeRevenue = idleFeeRevenue;
        this.byDay = byDay;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getEnergyRevenue() {
        return energyRevenue;
    }

    public void setEnergyRevenue(BigDecimal energyRevenue) {
        this.energyRevenue = energyRevenue;
    }

    public BigDecimal getTimeRevenue() {
        return timeRevenue;
    }

    public void setTimeRevenue(BigDecimal timeRevenue) {
        this.timeRevenue = timeRevenue;
    }

    public BigDecimal getIdleFeeRevenue() {
        return idleFeeRevenue;
    }

    public void setIdleFeeRevenue(BigDecimal idleFeeRevenue) {
        this.idleFeeRevenue = idleFeeRevenue;
    }

    public List<RevenueByDayDTO> getByDay() {
        return byDay;
    }

    public void setByDay(List<RevenueByDayDTO> byDay) {
        this.byDay = byDay;
    }
}

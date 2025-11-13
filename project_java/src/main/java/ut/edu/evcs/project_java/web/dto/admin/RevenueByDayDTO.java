package ut.edu.evcs.project_java.web.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RevenueByDayDTO {

    private LocalDate day;
    private BigDecimal totalRevenue;
    private BigDecimal energyRevenue;
    private BigDecimal timeRevenue;
    private BigDecimal idleFeeRevenue;

    public RevenueByDayDTO() {
    }

    public RevenueByDayDTO(LocalDate day,
                           BigDecimal totalRevenue,
                           BigDecimal energyRevenue,
                           BigDecimal timeRevenue,
                           BigDecimal idleFeeRevenue) {
        this.day = day;
        this.totalRevenue = totalRevenue;
        this.energyRevenue = energyRevenue;
        this.timeRevenue = timeRevenue;
        this.idleFeeRevenue = idleFeeRevenue;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
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
}

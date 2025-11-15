package ut.edu.evcs.project_java.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Thông tin phiên sạc trả về")
public class SessionResponse {
    private String id;
    private String driverId;
    private String vehicleId;
    private String connectorId;
    private String reservationId;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double kwhDelivered;
    private BigDecimal energyCost;
    private BigDecimal timeCost;
    private BigDecimal idleFee;
    private BigDecimal totalCost;
    private String stationName;
    private String connectorType;
    private BigDecimal unitPriceVnd;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public String getConnectorId() { return connectorId; }
    public void setConnectorId(String connectorId) { this.connectorId = connectorId; }
    
    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public double getKwhDelivered() { return kwhDelivered; }
    public void setKwhDelivered(double kwhDelivered) { this.kwhDelivered = kwhDelivered; }

    public BigDecimal getEnergyCost() { return energyCost; }
    public void setEnergyCost(BigDecimal energyCost) { this.energyCost = energyCost; }

    public BigDecimal getTimeCost() { return timeCost; }
    public void setTimeCost(BigDecimal timeCost) { this.timeCost = timeCost; }

    public BigDecimal getIdleFee() { return idleFee; }
    public void setIdleFee(BigDecimal idleFee) { this.idleFee = idleFee; }
    
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public String getConnectorType() { return connectorType; }
    public void setConnectorType(String connectorType) { this.connectorType = connectorType; }

    public BigDecimal getUnitPriceVnd() { return unitPriceVnd; }
    public void setUnitPriceVnd(BigDecimal unitPriceVnd) { this.unitPriceVnd = unitPriceVnd; }
}

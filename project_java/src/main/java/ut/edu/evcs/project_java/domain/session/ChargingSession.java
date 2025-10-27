package ut.edu.evcs.project_java.domain.session;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sessions")
public class ChargingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "driver_id", nullable = false)
    private String driverId;

    @Column(name = "vehicle_id")
    private String vehicleId;

    @Column(name = "connector_id", nullable = false)
    private String connectorId;

    @Column(name = "reservation_id")
    private String reservationId;

    @Column(nullable = false, length = 20)
    private String status; // STARTED, STOPPED, FAILED

    // Dùng default CURRENT_TIMESTAMP ở DB -> không insert/update từ app (tuỳ bạn)
    @Column(name = "start_time", insertable = false, updatable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "kwh_delivered", nullable = false)
    private double kwhDelivered = 0d;

    @Column(name = "energy_cost", nullable = false)
    private BigDecimal energyCost = BigDecimal.ZERO;

    @Column(name = "time_cost", nullable = false)
    private BigDecimal timeCost = BigDecimal.ZERO;

    @Column(name = "idle_fee", nullable = false)
    private BigDecimal idleFee = BigDecimal.ZERO;

    @Column(name = "total_cost", nullable = false)
    private BigDecimal totalCost = BigDecimal.ZERO;

    public ChargingSession() {}

    public ChargingSession(String id, String driverId, String vehicleId, String connectorId,
                           String reservationId, String status, LocalDateTime startTime,
                           LocalDateTime endTime, double kwhDelivered, BigDecimal energyCost,
                           BigDecimal timeCost, BigDecimal idleFee, BigDecimal totalCost) {
        this.id = id;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.connectorId = connectorId;
        this.reservationId = reservationId;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.kwhDelivered = kwhDelivered;
        this.energyCost = energyCost;
        this.timeCost = timeCost;
        this.idleFee = idleFee;
        this.totalCost = totalCost;
    }

    // Getters/Setters
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

    @Override
    public String toString() {
        return "ChargingSession{" +
                "id='" + id + '\'' +
                ", driverId='" + driverId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", connectorId='" + connectorId + '\'' +
                ", reservationId='" + reservationId + '\'' +
                ", status='" + status + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", kwhDelivered=" + kwhDelivered +
                ", energyCost=" + energyCost +
                ", timeCost=" + timeCost +
                ", idleFee=" + idleFee +
                ", totalCost=" + totalCost +
                '}';
    }
}

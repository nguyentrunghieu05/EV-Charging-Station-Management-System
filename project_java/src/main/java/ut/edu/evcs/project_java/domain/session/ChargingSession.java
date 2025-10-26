package ut.edu.evcs.project_java.domain.session;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "sessions")
public class ChargingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String driverId;
    private String vehicleId;
    private String connectorId;
    private String reservationId;
    private String status; // STARTED, STOPPED, FAILED
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private double kwhDelivered;
    private double energyCost;
    private double timeCost;
    private double idleFee;
    private double totalCost;

    public ChargingSession() {
    }

    public ChargingSession(String id, String driverId, String vehicleId, String connectorId,
                           String reservationId, String status, OffsetDateTime startTime,
                           OffsetDateTime endTime, double kwhDelivered, double energyCost,
                           double timeCost, double idleFee, double totalCost) {
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

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return this.driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getVehicleId() {
        return this.vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getConnectorId() {
        return this.connectorId;
    }

    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }

    public String getReservationId() {
        return this.reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    public OffsetDateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
    }

    public double getKwhDelivered() {
        return this.kwhDelivered;
    }

    public void setKwhDelivered(double kwhDelivered) {
        this.kwhDelivered = kwhDelivered;
    }

    public double getEnergyCost() {
        return this.energyCost;
    }

    public void setEnergyCost(double energyCost) {
        this.energyCost = energyCost;
    }

    public double getTimeCost() {
        return this.timeCost;
    }

    public void setTimeCost(double timeCost) {
        this.timeCost = timeCost;
    }

    public double getIdleFee() {
        return this.idleFee;
    }

    public void setIdleFee(double idleFee) {
        this.idleFee = idleFee;
    }

    public double getTotalCost() {
        return this.totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

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

package ut.edu.evcs.project_java.domain.session;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "driver_id", nullable = false)
    private String driverId;

    @Column(name = "connector_id", nullable = false)
    private String connectorId;

    @Column(name = "start_window", nullable = false)
    private LocalDateTime startWindow;

    @Column(name = "end_window", nullable = false)
    private LocalDateTime endWindow;

    @Column(nullable = false, length = 20)
    private String status; // PENDING, CONFIRMED, CANCELLED

    public Reservation() {}

    public Reservation(String id, String driverId, String connectorId,
                       LocalDateTime startWindow, LocalDateTime endWindow, String status) {
        this.id = id;
        this.driverId = driverId;
        this.connectorId = connectorId;
        this.startWindow = startWindow;
        this.endWindow = endWindow;
        this.status = status;
    }

    // Getters/Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public String getConnectorId() { return connectorId; }
    public void setConnectorId(String connectorId) { this.connectorId = connectorId; }

    public LocalDateTime getStartWindow() { return startWindow; }
    public void setStartWindow(LocalDateTime startWindow) { this.startWindow = startWindow; }

    public LocalDateTime getEndWindow() { return endWindow; }
    public void setEndWindow(LocalDateTime endWindow) { this.endWindow = endWindow; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Reservation{" +
                "id='" + id + '\'' +
                ", driverId='" + driverId + '\'' +
                ", connectorId='" + connectorId + '\'' +
                ", startWindow=" + startWindow +
                ", endWindow=" + endWindow +
                ", status='" + status + '\'' +
                '}';
    }
}

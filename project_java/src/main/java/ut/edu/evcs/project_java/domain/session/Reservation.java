package ut.edu.evcs.project_java.domain.session;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String driverId;
    private String connectorId;
    private OffsetDateTime startWindow;
    private OffsetDateTime endWindow;
    private String status; // PENDING, CONFIRMED, CANCELLED

    public Reservation() {
    }

    public Reservation(String id, String driverId, String connectorId,
                       OffsetDateTime startWindow, OffsetDateTime endWindow, String status) {
        this.id = id;
        this.driverId = driverId;
        this.connectorId = connectorId;
        this.startWindow = startWindow;
        this.endWindow = endWindow;
        this.status = status;
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

    public String getConnectorId() {
        return this.connectorId;
    }

    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }

    public OffsetDateTime getStartWindow() {
        return this.startWindow;
    }

    public void setStartWindow(OffsetDateTime startWindow) {
        this.startWindow = startWindow;
    }

    public OffsetDateTime getEndWindow() {
        return this.endWindow;
    }

    public void setEndWindow(OffsetDateTime endWindow) {
        this.endWindow = endWindow;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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

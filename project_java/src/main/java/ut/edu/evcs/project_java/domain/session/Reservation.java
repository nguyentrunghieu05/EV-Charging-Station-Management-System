package ut.edu.evcs.project_java.domain.session;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // SỬA: Đổi từ IDENTITY (Long) sang UUID (String)
    private String id; // SỬA: Đổi từ Long sang String

    @Column(name = "driver_id", nullable = false)
    private String driverId; // SỬA: Đổi từ userId (Long) sang driverId (String)

    @Column(name = "connector_id", nullable = false)
    private String connectorId;

    @Column(name = "start_window", nullable = false)
    private LocalDateTime startWindow;

    @Column(name = "end_window", nullable = false)
    private LocalDateTime endWindow;

    @Column(nullable = false)
    private String status;

    // No-arg constructor
    public Reservation() {
    }

    // All-args constructor (đã cập nhật)
    public Reservation(String id, String driverId, String connectorId, LocalDateTime startWindow, LocalDateTime endWindow, String status) {
        this.id = id;
        this.driverId = driverId;
        this.connectorId = connectorId;
        this.startWindow = startWindow;
        this.endWindow = endWindow;
        this.status = status;
    }

    // Getters and setters (đã cập nhật)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }

    public LocalDateTime getStartWindow() {
        return startWindow;
    }

    public void setStartWindow(LocalDateTime startWindow) {
        this.startWindow = startWindow;
    }

    public LocalDateTime getEndWindow() {
        return endWindow;
    }

    public void setEndWindow(LocalDateTime endWindow) {
        this.endWindow = endWindow;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String driverId;
        private String connectorId;
        private LocalDateTime startWindow;
        private LocalDateTime endWindow;
        private String status;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder driverId(String driverId) {
            this.driverId = driverId;
            return this;
        }

        public Builder connectorId(String connectorId) {
            this.connectorId = connectorId;
            return this;
        }

        public Builder startWindow(LocalDateTime startWindow) {
            this.startWindow = startWindow;
            return this;
        }

        public Builder endWindow(LocalDateTime endWindow) {
            this.endWindow = endWindow;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Reservation build() {
            return new Reservation(id, driverId, connectorId, startWindow, endWindow, status);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(driverId, that.driverId) &&
                Objects.equals(connectorId, that.connectorId) &&
                Objects.equals(startWindow, that.startWindow) &&
                Objects.equals(endWindow, that.endWindow) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, driverId, connectorId, startWindow, endWindow, status);
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

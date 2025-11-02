package ut.edu.evcs.project_java.domain.session;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "station_id", nullable = false)
    private Long stationId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String status;

    @Column(name = "connector_id")
    private String connectorId;

    @Column(name = "start_window")
    private LocalDateTime startWindow;

    @Column(name = "end_window")
    private LocalDateTime endWindow;

    // No-arg constructor
    public Reservation() {
    }

    // All-args constructor
    public Reservation(Long id, Long userId, Long stationId, LocalDateTime startTime, LocalDateTime endTime, String status,
                      String connectorId, LocalDateTime startWindow, LocalDateTime endWindow) {
        this.id = id;
        this.userId = userId;
        this.stationId = stationId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.connectorId = connectorId;
        this.startWindow = startWindow;
        this.endWindow = endWindow;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public Object getDriverId() {
        return userId; // using userId as driverId
    }

    public static class Builder {

        private Long id;
        private Long userId;
        private Long stationId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String status;
        private String connectorId;
        private LocalDateTime startWindow;
        private LocalDateTime endWindow;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder stationId(Long stationId) {
            this.stationId = stationId;
            return this;
        }

        public Builder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
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

        public Reservation build() {
            return new Reservation(id, userId, stationId, startTime, endTime, status, connectorId, startWindow, endWindow);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id)
                && Objects.equals(userId, that.userId)
                && Objects.equals(stationId, that.stationId)
                && Objects.equals(startTime, that.startTime)
                && Objects.equals(endTime, that.endTime)
                && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, stationId, startTime, endTime, status);
    }

    @Override
    public String toString() {
        return "Reservation{"
                + "id=" + id
                + ", userId=" + userId
                + ", stationId=" + stationId
                + ", startTime=" + startTime
                + ", endTime=" + endTime
                + ", status='" + status + '\''
                + '}';
    }
}

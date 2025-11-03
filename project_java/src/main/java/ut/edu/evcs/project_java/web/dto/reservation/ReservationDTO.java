package ut.edu.evcs.project_java.web.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO trả về thông tin reservation
 * FILE: src/main/java/ut/edu/evcs/project_java/web/dto/reservation/ReservationDTO.java
 */
@Schema(description = "Thông tin đặt chỗ sạc")
public class ReservationDTO {
    
    @Schema(description = "ID reservation")
    private String id;
    
    @Schema(description = "ID tài xế")
    private String driverId;
    
    @Schema(description = "ID connector")
    private String connectorId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Thời gian bắt đầu")
    private LocalDateTime startWindow;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Thời gian kết thúc")
    private LocalDateTime endWindow;
    
    @Schema(description = "Trạng thái", example = "PENDING")
    private String status;
    
    @Schema(description = "ID xe")
    private String vehicleId;

    // Constructors
    public ReservationDTO() {}

    public ReservationDTO(String id, String driverId, String connectorId, 
                         LocalDateTime startWindow, LocalDateTime endWindow, String status) {
        this.id = id;
        this.driverId = driverId;
        this.connectorId = connectorId;
        this.startWindow = startWindow;
        this.endWindow = endWindow;
        this.status = status;
    }

    // Getters and setters
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

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
}
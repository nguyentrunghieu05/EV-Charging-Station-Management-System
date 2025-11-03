package ut.edu.evcs.project_java.web.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * DTO để tạo reservation mới
 * FILE: src/main/java/ut/edu/evcs/project_java/web/dto/reservation/CreateReservationRequest.java
 */
@Schema(description = "Yêu cầu tạo đặt chỗ sạc mới")
public class CreateReservationRequest {
    
    @NotBlank
    @Schema(description = "ID tài xế", example = "driver-uuid-123")
    private String driverId;
    
    @NotBlank
    @Schema(description = "ID connector cần đặt", example = "connector-uuid-456")
    private String connectorId;
    
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Thời gian bắt đầu dự kiến", example = "2025-11-03T14:00:00")
    private LocalDateTime startWindow;
    
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Thời gian kết thúc dự kiến", example = "2025-11-03T16:00:00")
    private LocalDateTime endWindow;
    
    @Schema(description = "ID xe (tùy chọn)", example = "vehicle-uuid-789")
    private String vehicleId;

    // Constructors
    public CreateReservationRequest() {}

    public CreateReservationRequest(String driverId, String connectorId, 
                                   LocalDateTime startWindow, LocalDateTime endWindow) {
        this.driverId = driverId;
        this.connectorId = connectorId;
        this.startWindow = startWindow;
        this.endWindow = endWindow;
    }

    // Getters and setters
    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public String getConnectorId() { return connectorId; }
    public void setConnectorId(String connectorId) { this.connectorId = connectorId; }

    public LocalDateTime getStartWindow() { return startWindow; }
    public void setStartWindow(LocalDateTime startWindow) { this.startWindow = startWindow; }

    public LocalDateTime getEndWindow() { return endWindow; }
    public void setEndWindow(LocalDateTime endWindow) { this.endWindow = endWindow; }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
}
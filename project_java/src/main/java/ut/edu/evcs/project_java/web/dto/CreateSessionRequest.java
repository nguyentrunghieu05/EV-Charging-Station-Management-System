package ut.edu.evcs.project_java.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload để bắt đầu phiên sạc")
public class CreateSessionRequest {

    @NotBlank
    @Schema(example = "driver-uuid", description = "ID tài xế")
    private String driverId;

    @NotBlank
    @Schema(example = "vehicle-uuid", description = "ID xe")
    private String vehicleId;

    @NotBlank
    @Schema(example = "connector-uuid", description = "ID cổng sạc")
    private String connectorId;

    // getters/setters
    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public String getConnectorId() { return connectorId; }
    public void setConnectorId(String connectorId) { this.connectorId = connectorId; }
}

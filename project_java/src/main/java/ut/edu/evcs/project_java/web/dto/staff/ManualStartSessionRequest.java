package ut.edu.evcs.project_java.web.dto.staff;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to manually start a session")
public class ManualStartSessionRequest {
    @NotBlank
    private String driverId;
    
    @NotBlank
    private String connectorId;
    
    private String vehicleId;
    
    @Schema(description = "Staff notes")
    private String notes;

    // Getters/Setters
    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    
    public String getConnectorId() { return connectorId; }
    public void setConnectorId(String connectorId) { this.connectorId = connectorId; }
    
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
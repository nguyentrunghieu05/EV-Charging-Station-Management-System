package ut.edu.evcs.project_java.web.dto.staff;

import jakarta.validation.constraints.NotBlank;

public class ReportIncidentRequest {
    @NotBlank
    private String stationId;
    
    private String connectorId;
    
    @NotBlank
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    
    @NotBlank
    private String description;
    
    private String attachmentUrl;

    // Getters/Setters
    public String getStationId() { return stationId; }
    public void setStationId(String stationId) { this.stationId = stationId; }
    
    public String getConnectorId() { return connectorId; }
    public void setConnectorId(String connectorId) { this.connectorId = connectorId; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
}

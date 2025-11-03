package ut.edu.evcs.project_java.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO cho thông tin connector đơn giản
 * FILE: src/main/java/ut/edu/evcs/project_java/web/dto/ConnectorInfoDTO.java
 */
@Schema(description = "Thông tin connector")
public class ConnectorInfoDTO {
    
    @Schema(description = "ID connector")
    private String id;
    
    @Schema(description = "Loại connector", example = "CCS")
    private String type;
    
    @Schema(description = "Dòng điện tối đa (A)", example = "200")
    private double maxCurrentA;
    
    @Schema(description = "Điện áp (V)", example = "400")
    private double voltageV;
    
    @Schema(description = "Đang bận hay không")
    private boolean occupied;

    // Constructors
    public ConnectorInfoDTO() {}

    public ConnectorInfoDTO(String id, String type, double maxCurrentA, 
                           double voltageV, boolean occupied) {
        this.id = id;
        this.type = type;
        this.maxCurrentA = maxCurrentA;
        this.voltageV = voltageV;
        this.occupied = occupied;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getMaxCurrentA() { return maxCurrentA; }
    public void setMaxCurrentA(double maxCurrentA) { this.maxCurrentA = maxCurrentA; }

    public double getVoltageV() { return voltageV; }
    public void setVoltageV(double voltageV) { this.voltageV = voltageV; }

    public boolean isOccupied() { return occupied; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }

    @Override
    public String toString() {
        return "ConnectorInfoDTO{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", maxCurrentA=" + maxCurrentA +
                ", voltageV=" + voltageV +
                ", occupied=" + occupied +
                '}';
    }
}
package ut.edu.evcs.project_java.web.dto.admin;

public class ChargingPointDetailDTO {
    private String id;
    private String code;
    private double maxPowerKW;
    private boolean online;
    private String stationName;
    private int connectorCount;
    private int occupiedConnectors;

    public ChargingPointDetailDTO() {
    }

    public ChargingPointDetailDTO(String id, String code, double maxPowerKW, boolean online,
            String stationName, int connectorCount, int occupiedConnectors) {
        this.id = id;
        this.code = code;
        this.maxPowerKW = maxPowerKW;
        this.online = online;
        this.stationName = stationName;
        this.connectorCount = connectorCount;
        this.occupiedConnectors = occupiedConnectors;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getMaxPowerKW() {
        return maxPowerKW;
    }

    public void setMaxPowerKW(double maxPowerKW) {
        this.maxPowerKW = maxPowerKW;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public int getConnectorCount() {
        return connectorCount;
    }

    public void setConnectorCount(int connectorCount) {
        this.connectorCount = connectorCount;
    }

    public int getOccupiedConnectors() {
        return occupiedConnectors;
    }

    public void setOccupiedConnectors(int occupiedConnectors) {
        this.occupiedConnectors = occupiedConnectors;
    }
}

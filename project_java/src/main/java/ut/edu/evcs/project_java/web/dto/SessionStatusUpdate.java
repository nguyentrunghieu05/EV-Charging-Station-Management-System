package ut.edu.evcs.project_java.web.dto;

public class SessionStatusUpdate {

    private String sessionId;
    private String connectorId;
    private String status; // ví dụ: "CHARGING", "STOPPED", "COMPLETED"
    private double energyDelivered; // kWh
    private double currentPower; // kW

    public SessionStatusUpdate() {
    }

    public SessionStatusUpdate(String sessionId, String connectorId, String status, double energyDelivered, double currentPower) {
        this.sessionId = sessionId;
        this.connectorId = connectorId;
        this.status = status;
        this.energyDelivered = energyDelivered;
        this.currentPower = currentPower;
    }

    // Getters và Setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getEnergyDelivered() {
        return energyDelivered;
    }

    public void setEnergyDelivered(double energyDelivered) {
        this.energyDelivered = energyDelivered;
    }

    public double getCurrentPower() {
        return currentPower;
    }

    public void setCurrentPower(double currentPower) {
        this.currentPower = currentPower;
    }

    @Override
    public String toString() {
        return "SessionStatusUpdate{" +
                "sessionId='" + sessionId + '\'' +
                ", connectorId='" + connectorId + '\'' +
                ", status='" + status + '\'' +
                ", energyDelivered=" + energyDelivered +
                ", currentPower=" + currentPower +
                '}';
    }
}

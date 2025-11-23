package ut.edu.evcs.project_java.web.dto.admin;

import java.util.List;

public class StationDetailDTO {
    private String id;
    private String name;
    private String address;
    private double lat;
    private double lng;
    private String status;
    private int availablePorts;
    private int totalChargingPoints;
    private int onlineChargingPoints;
    private List<ChargingPointDetailDTO> chargingPoints;

    public StationDetailDTO() {
    }

    public StationDetailDTO(String id, String name, String address, double lat, double lng,
            String status, int availablePorts, int totalChargingPoints,
            int onlineChargingPoints) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.status = status;
        this.availablePorts = availablePorts;
        this.totalChargingPoints = totalChargingPoints;
        this.onlineChargingPoints = onlineChargingPoints;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAvailablePorts() {
        return availablePorts;
    }

    public void setAvailablePorts(int availablePorts) {
        this.availablePorts = availablePorts;
    }

    public int getTotalChargingPoints() {
        return totalChargingPoints;
    }

    public void setTotalChargingPoints(int totalChargingPoints) {
        this.totalChargingPoints = totalChargingPoints;
    }

    public int getOnlineChargingPoints() {
        return onlineChargingPoints;
    }

    public void setOnlineChargingPoints(int onlineChargingPoints) {
        this.onlineChargingPoints = onlineChargingPoints;
    }

    public List<ChargingPointDetailDTO> getChargingPoints() {
        return chargingPoints;
    }

    public void setChargingPoints(List<ChargingPointDetailDTO> chargingPoints) {
        this.chargingPoints = chargingPoints;
    }
}

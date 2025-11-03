package ut.edu.evcs.project_java.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Thông tin trạm sạc")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StationDTO {
    
    @Schema(description = "ID trạm sạc", example = "uuid-123")
    private String id;
    
    @Schema(description = "Tên trạm", example = "EVCS Nguyễn Huệ")
    private String name;
    
    @Schema(description = "Địa chỉ", example = "Đường Nguyễn Huệ, Quận 1, TP.HCM")
    private String address;
    
    @Schema(description = "Vĩ độ", example = "10.7744")
    private double lat;
    
    @Schema(description = "Kinh độ", example = "106.7019")
    private double lng;
    
    @Schema(description = "Trạng thái", example = "ONLINE")
    private String status;
    
    @Schema(description = "Khoảng cách từ vị trí hiện tại (km)", example = "2.5")
    private Double distance;
    
    @Schema(description = "Số connector available")
    private Integer availableConnectors;

    // Constructors
    public StationDTO() {}

    public StationDTO(String id, String name, String address, double lat, double lng, String status) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.status = status;
    }

    public StationDTO(String id, String name, String address, double lat, double lng, String status, Double distance) {
        this(id, name, address, lat, lng, status);
        this.distance = distance;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    public Integer getAvailableConnectors() { return availableConnectors; }
    public void setAvailableConnectors(Integer availableConnectors) { 
        this.availableConnectors = availableConnectors; 
    }

    @Override
    public String toString() {
        return "StationDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", status='" + status + '\'' +
                (distance != null ? ", distance=" + String.format("%.2f", distance) + "km" : "") +
                '}';
    }
}
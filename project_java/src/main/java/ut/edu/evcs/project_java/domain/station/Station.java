package ut.edu.evcs.project_java.domain.station;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point; // Cần thiết cho V7 migration

import java.util.Objects;

@Entity
@Table(name = "stations")
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // SỬA: Đổi từ IDENTITY (Long) sang UUID (String)
    private String id; // SỬA: Đổi từ Long sang String

    @Column(nullable = false)
    private String name;

    private String address;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lng;

    @Column(name = "status")
    private String status; 

    @Column(name = "available_ports", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int availablePorts;

    @Column(name = "location", nullable = false, columnDefinition = "POINT NOT NULL")
    private Point location;

    // Constructor rỗng
    public Station() {
    }

    // Constructor đầy đủ (đã cập nhật id thành String)
    public Station(String id, String name, String address, double lat, double lng, String status, int availablePorts, Point location) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.status = status;
        this.availablePorts = availablePorts;
        this.location = location;
    }

    // --- Getters và Setters (đã cập nhật id thành String) ---

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

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    // --- toString, equals, và hashCode (đã cập nhật) ---

    @Override
    public String toString() {
        return "Station{" +
                "id='" + id + '\'' + // Sửa
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", status='" + status + '\'' +
                ", availablePorts=" + availablePorts +
                ", location=" + location +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Double.compare(station.lat, lat) == 0 &&
                Double.compare(station.lng, lng) == 0 &&
                availablePorts == station.availablePorts &&
                Objects.equals(id, station.id) && // Hoạt động cho cả String
                Objects.equals(name, station.name) &&
                Objects.equals(address, station.address) &&
                Objects.equals(status, station.status) &&
                Objects.equals(location, station.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, lat, lng, status, availablePorts, location);
    }
}


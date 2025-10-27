package ut.edu.evcs.project_java.domain.vehicle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "driver_id", nullable = false)
    private String driverId; // FK to users.id (EV_DRIVER)
    private String brand;
    private String model;

     @Column(name = "plate_no", nullable = false, unique = true)
    private String plateNo;
    @Column(name = "battery_capacity_kwh", nullable = false)
    private double batteryCapacityKWh;

    @Column(name = "connector_supported")
    private String connectorSupported;

    public Vehicle() {
    }

    public Vehicle(String id, String driverId, String brand, String model, String plateNo, double batteryCapacityKWh, String connectorSupported) {
        this.id = id;
        this.driverId = driverId;
        this.brand = brand;
        this.model = model;
        this.plateNo = plateNo;
        this.batteryCapacityKWh = batteryCapacityKWh;
        this.connectorSupported = connectorSupported;
    }

    public String getId() {return this.id;}

    public void setId(String id) {this.id = id;}

    public String getDriverId() {
        return this.driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getBrand() {
        return this.brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPlateNo() {
        return this.plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public double getBatteryCapacityKWh() {
        return this.batteryCapacityKWh;
    }

    public void setBatteryCapacityKWh(double batteryCapacityKWh) {
        this.batteryCapacityKWh = batteryCapacityKWh;
    }

    public String getConnectorSupported() {
        return this.connectorSupported;
    }

    public void setConnectorSupported(String connectorSupported) {
        this.connectorSupported = connectorSupported;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", driverId='" + driverId + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", plateNo='" + plateNo + '\'' +
                ", batteryCapacityKWh=" + batteryCapacityKWh +
                ", connectorSupported='" + connectorSupported + '\'' +
                '}';
    }
}

package ut.edu.evcs.project_java.domain.vehicle;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "vehicles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder

public class Vehicle {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String driverId; // FK to users.id (EV_DRIVER)
    private String brand;
    private String model;
    private String plateNo;
    private double batteryCapacityKWh;
    private String connectorSupported;
}

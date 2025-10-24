package ut.edu.evcs.project_java.domain.station;

import jakarta.persistence.*;
import lombok.*;


@Entity @Table(name = "connectors")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Connector {
@Id @GeneratedValue(strategy = GenerationType.UUID)
private String id;
private String type; // CCS/CHAdeMO/AC
private double maxCurrentA;
private double voltageV;
private boolean occupied;
private String qrCode;


@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "point_id")
private ChargingPoint chargingPoint;
}

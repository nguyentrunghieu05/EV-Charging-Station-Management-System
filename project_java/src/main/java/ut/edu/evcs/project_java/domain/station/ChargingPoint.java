package ut.edu.evcs.project_java.domain.station;

import jakarta.persistence.*;
import lombok.*;


@Entity @Table(name = "charging_points")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChargingPoint {
@Id @GeneratedValue(strategy = GenerationType.UUID)
private String id;
private String code;
private double maxPowerKW;
private boolean online;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "station_id")
private Station station;
}

package ut.edu.evcs.project_java.domain.station;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "stations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Station {
@Id @GeneratedValue(strategy = GenerationType.UUID)
private String id;
private String name;
private String address;
private double lat;
private double lng;
private String status; // ONLINE/OFFLINE/MAINTENANCE
}

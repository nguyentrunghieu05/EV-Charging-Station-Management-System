package ut.edu.evcs.project_java.domain.session;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity @Table(name = "reservations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String driverId;
    private String connectorId;
    private OffsetDateTime startWindow;
    private OffsetDateTime endWindow;
    private String status; // PENDING, CONFIRMED, CANCELLED
}

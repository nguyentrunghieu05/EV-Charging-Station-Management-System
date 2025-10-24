package ut.edu.evcs.project_java.domain.session;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity @Table(name = "sessions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChargingSession {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String driverId;
    private String vehicleId;
    private String connectorId;
    private String reservationId;
    private String status; // STARTED, STOPPED, FAILED
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private double kwhDelivered;
    private double energyCost;
    private double timeCost;
    private double idleFee;
    private double totalCost;
}

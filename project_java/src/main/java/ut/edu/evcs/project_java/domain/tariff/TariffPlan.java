package ut.edu.evcs.project_java.domain.tariff;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "tariffs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder

public class TariffPlan {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String mode; // PER_KWH, PER_MINUTE, HYBRID, SUBSCRIPTION
    private double pricePerKWh;
    private double pricePerMinute;
    private double idleFeePerMinute;
    private String timeOfUseRules;
    private boolean active;
}

package ut.edu.evcs.project_java.domain.billing;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "wallets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Wallet {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String ownerUserId;
    private double balance;
    private String currency;
}

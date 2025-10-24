package ut.edu.evcs.project_java.domain.billing;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity @Table(name = "invoices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Invoice {
@Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String driverId;
    private String sessionId;
    private double amount;
    private double taxAmount;
    private String currency;
    private OffsetDateTime issuedAt;
    private String status; // DRAFT, ISSUED, PAID, VOID
    private String pdfUrl;
}

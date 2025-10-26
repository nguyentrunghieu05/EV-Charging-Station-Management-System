package ut.edu.evcs.project_java.domain.billing;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String driverId;
    private String sessionId;
    private double amount;
    private double taxAmount;
    private String currency;
    private OffsetDateTime issuedAt;
    private String status; // DRAFT, ISSUED, PAID, VOID
    private String pdfUrl;

    public Invoice() {
    }

    public Invoice(String id, String driverId, String sessionId, double amount,
                   double taxAmount, String currency, OffsetDateTime issuedAt,
                   String status, String pdfUrl) {
        this.id = id;
        this.driverId = driverId;
        this.sessionId = sessionId;
        this.amount = amount;
        this.taxAmount = taxAmount;
        this.currency = currency;
        this.issuedAt = issuedAt;
        this.status = status;
        this.pdfUrl = pdfUrl;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return this.driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getTaxAmount() {
        return this.taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public OffsetDateTime getIssuedAt() {
        return this.issuedAt;
    }

    public void setIssuedAt(OffsetDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPdfUrl() {
        return this.pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id='" + id + '\'' +
                ", driverId='" + driverId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", amount=" + amount +
                ", taxAmount=" + taxAmount +
                ", currency='" + currency + '\'' +
                ", issuedAt=" + issuedAt +
                ", status='" + status + '\'' +
                ", pdfUrl='" + pdfUrl + '\'' +
                '}';
    }
}

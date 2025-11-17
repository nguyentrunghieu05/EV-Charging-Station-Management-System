package ut.edu.evcs.project_java.domain.billing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "invoices")
public class Invoice {
//
    @Column(name = "invoice_no", length = 30, unique = true)
    private String invoiceNo;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "driver_id", nullable = false)
    private String driverId;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "energy_cost", nullable = false)
    private BigDecimal energyCost = BigDecimal.ZERO;

    @Column(name = "time_cost", nullable = false)
    private BigDecimal timeCost = BigDecimal.ZERO;

    @Column(name = "idle_fee", nullable = false)
    private BigDecimal idleFee = BigDecimal.ZERO;

    @Column(name = "service_fee", nullable = false)
    private BigDecimal serviceFee = BigDecimal.ZERO;

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(nullable = false, length = 10)
    private String currency = "VND";

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "issued_at", insertable = false, updatable = false)
    private LocalDateTime issuedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "pdf_url", length = 255)
    private String pdfUrl;

    public Invoice() {}

    public Invoice(String id,
                   String driverId,
                   String sessionId,
                   BigDecimal totalAmount,
                   BigDecimal energyCost,
                   BigDecimal timeCost,
                   BigDecimal idleFee,
                   BigDecimal serviceFee,
                   BigDecimal subtotal,
                   BigDecimal taxAmount,
                   String currency,
                   String status,
                   LocalDateTime issuedAt,
                   LocalDateTime paidAt,
                   String pdfUrl) {
        this.id = id;
        this.driverId = driverId;
        this.sessionId = sessionId;
        this.totalAmount = totalAmount;
        this.energyCost = energyCost;
        this.timeCost = timeCost;
        this.idleFee = idleFee;
        this.serviceFee = serviceFee;
        this.subtotal = subtotal;
        this.taxAmount = taxAmount;
        this.currency = currency;
        this.status = status;
        this.issuedAt = issuedAt;
        this.paidAt = paidAt;
        this.pdfUrl = pdfUrl;
    }

    // --- getters/setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getEnergyCost() { return energyCost; }
    public void setEnergyCost(BigDecimal energyCost) { this.energyCost = energyCost; }

    public BigDecimal getTimeCost() { return timeCost; }
    public void setTimeCost(BigDecimal timeCost) { this.timeCost = timeCost; }

    public BigDecimal getIdleFee() { return idleFee; }
    public void setIdleFee(BigDecimal idleFee) { this.idleFee = idleFee; }

    public BigDecimal getServiceFee() { return serviceFee; }
    public void setServiceFee(BigDecimal serviceFee) { this.serviceFee = serviceFee; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }

    public String getInvoiceNo() { return invoiceNo; }

    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }

    
    @Override
    public String toString() {
        return "Invoice{" +
                "id='" + id + '\'' +
                ", driverId='" + driverId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", energyCost=" + energyCost +
                ", timeCost=" + timeCost +
                ", idleFee=" + idleFee +
                ", serviceFee=" + serviceFee +
                ", subtotal=" + subtotal +
                ", taxAmount=" + taxAmount +
                ", totalAmount=" + totalAmount +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", issuedAt=" + issuedAt +
                ", paidAt=" + paidAt +
                ", pdfUrl='" + pdfUrl + '\'' +
                '}';
    }
}

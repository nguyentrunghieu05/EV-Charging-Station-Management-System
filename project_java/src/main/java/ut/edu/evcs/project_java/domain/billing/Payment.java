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
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "invoice_id", nullable = false)
    private String invoiceId;

    @Column(nullable = false)
    private String method; // e.g. "EWallet", "Banking", "InternalWallet"

    @Column(nullable = false)
    private String status; // e.g. "PENDING", "SETTLED", "FAILED"

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "provider_ref")
    private String providerRef; // reference id from payment gateway

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;


    public Payment() {}

    public Payment(String invoiceId, String method, String status, BigDecimal amount, String providerRef) {
        this.invoiceId = invoiceId;
        this.method = method;
        this.status = status;
        this.amount = amount;
        this.providerRef = providerRef;
    }


    public String getId() {
        return id;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getProviderRef() {
        return providerRef;
    }

    public void setProviderRef(String providerRef) {
        this.providerRef = providerRef;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Utility
    public boolean isSettled() {
        return "SETTLED".equalsIgnoreCase(this.status);
    }
}

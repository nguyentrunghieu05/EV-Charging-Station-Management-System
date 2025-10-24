package ut.edu.evcs.project_java.domain.billing;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String invoiceId;

    @Column(nullable = false)
    private String method; // e.g. "EWallet", "Banking", "InternalWallet"

    @Column(nullable = false)
    private String status; // e.g. "PENDING", "SETTLED", "FAILED"

    @Column(nullable = false)
    private double amount;

    private String providerRef; // reference id from payment gateway

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();


    public Payment() {}

    public Payment(String invoiceId, String method, String status, double amount, String providerRef) {
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getProviderRef() {
        return providerRef;
    }

    public void setProviderRef(String providerRef) {
        this.providerRef = providerRef;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // Utility
    public boolean isSettled() {
        return "SETTLED".equalsIgnoreCase(this.status);
    }
}

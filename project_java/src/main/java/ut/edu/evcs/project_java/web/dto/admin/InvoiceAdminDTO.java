package ut.edu.evcs.project_java.web.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceAdminDTO {
    private String id;
    private String userName;
    private String sessionId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime issuedAt;

    public InvoiceAdminDTO(String id, String userName, String sessionId, BigDecimal totalAmount, String status,
            LocalDateTime issuedAt) {
        this.id = id;
        this.userName = userName;
        this.sessionId = sessionId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.issuedAt = issuedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }
}

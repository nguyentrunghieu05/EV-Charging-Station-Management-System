package ut.edu.evcs.project_java.web.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserSubscriptionDTO {
    private String id;
    private String userId;
    private String userName;
    private String planId;
    private String planName;
    private String planType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private BigDecimal kwhUsed;

    public UserSubscriptionDTO() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getKwhUsed() {
        return kwhUsed;
    }

    public void setKwhUsed(BigDecimal kwhUsed) {
        this.kwhUsed = kwhUsed;
    }
}

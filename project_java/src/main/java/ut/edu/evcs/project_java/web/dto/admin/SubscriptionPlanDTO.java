package ut.edu.evcs.project_java.web.dto.admin;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SubscriptionPlanDTO {
    private String id;

    @NotBlank(message = "Tên gói không được để trống")
    private String name;

    @NotBlank(message = "Loại gói không được để trống")
    private String planType;

    @NotNull(message = "Giá không được để trống")
    private BigDecimal price;

    @NotNull(message = "Thời hạn không được để trống")
    private Integer durationDays;

    private BigDecimal discountPercent;
    private BigDecimal freeKwh;
    private Boolean priorityAccess;
    private String description;
    private Boolean isActive;

    public SubscriptionPlanDTO() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public BigDecimal getFreeKwh() {
        return freeKwh;
    }

    public void setFreeKwh(BigDecimal freeKwh) {
        this.freeKwh = freeKwh;
    }

    public Boolean getPriorityAccess() {
        return priorityAccess;
    }

    public void setPriorityAccess(Boolean priorityAccess) {
        this.priorityAccess = priorityAccess;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}

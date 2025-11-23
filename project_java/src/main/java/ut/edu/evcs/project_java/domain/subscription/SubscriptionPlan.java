package ut.edu.evcs.project_java.domain.subscription;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false, length = 50)
    private PlanType planType;

    @Column(nullable = false)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays = 30;

    @Column(name = "discount_percent")
    private BigDecimal discountPercent = BigDecimal.ZERO;

    @Column(name = "free_kwh")
    private BigDecimal freeKwh = BigDecimal.ZERO;

    @Column(name = "priority_access")
    private Boolean priorityAccess = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String description;

    public SubscriptionPlan() {
    }

    public SubscriptionPlan(String id, String name, PlanType planType, BigDecimal price, Integer durationDays,
                           BigDecimal discountPercent, BigDecimal freeKwh, Boolean priorityAccess, 
                           String description, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.planType = planType;
        this.price = price;
        this.durationDays = durationDays;
        this.discountPercent = discountPercent;
        this.freeKwh = freeKwh;
        this.priorityAccess = priorityAccess;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanType planType) {
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SubscriptionPlan{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", planType=" + planType +
                ", price=" + price +
                ", durationDays=" + durationDays +
                ", discountPercent=" + discountPercent +
                ", freeKwh=" + freeKwh +
                ", priorityAccess=" + priorityAccess +
                ", isActive=" + isActive +
                '}';
    }
}

package ut.edu.evcs.project_java.web.dto.admin;

import jakarta.validation.constraints.NotBlank;

public class CreateSubscriptionRequest {

    @NotBlank(message = "Plan ID không được để trống")
    private String planId;

    private Integer durationDays; // Optional: override default duration

    public CreateSubscriptionRequest() {
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }
}

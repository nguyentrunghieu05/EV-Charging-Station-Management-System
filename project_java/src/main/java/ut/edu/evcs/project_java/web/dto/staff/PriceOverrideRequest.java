package ut.edu.evcs.project_java.web.dto.staff;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PriceOverrideRequest {

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "newPricePerKwh must be > 0")
    private BigDecimal newPricePerKwh;

    @NotBlank
    private String reason;

    public BigDecimal getNewPricePerKwh() {
        return newPricePerKwh;
    }
    public void setNewPricePerKwh(BigDecimal newPricePerKwh) {
        this.newPricePerKwh = newPricePerKwh;
    }

    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
}

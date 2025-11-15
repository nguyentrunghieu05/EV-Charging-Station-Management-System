package ut.edu.evcs.project_java.web.dto.staff;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public class ManualStopSessionRequest {
    @NotNull
    private BigDecimal finalKwh;
    
    private String notes;

    public BigDecimal getFinalKwh() { return finalKwh; }
    public void setFinalKwh(BigDecimal finalKwh) { this.finalKwh = finalKwh; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
package ut.edu.evcs.project_java.web.dto.staff;

import jakarta.validation.constraints.NotBlank;

public class AssignIncidentRequest {

    @NotBlank
    private String staffId;

    public AssignIncidentRequest() {
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
}

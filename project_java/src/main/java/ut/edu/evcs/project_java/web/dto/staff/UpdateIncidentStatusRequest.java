package ut.edu.evcs.project_java.web.dto.staff;

import jakarta.validation.constraints.NotBlank;

public class UpdateIncidentStatusRequest {

    @NotBlank
    private String status;

    private String resolution;

    public UpdateIncidentStatusRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}

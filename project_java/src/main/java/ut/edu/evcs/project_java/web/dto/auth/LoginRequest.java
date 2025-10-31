package ut.edu.evcs.project_java.web.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    private String emailOrUsername;

    @NotBlank
    private String password;

    // getters/setters
    public String getEmailOrUsername() { return emailOrUsername; }
    public void setEmailOrUsername(String emailOrUsername) { this.emailOrUsername = emailOrUsername; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

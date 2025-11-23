package ut.edu.evcs.project_java.web.dto.auth;

public class LoginRequest {
    private String emailOrUsername;
    private String password;

    // Getters
    public String getEmailOrUsername() {
        return emailOrUsername;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setEmailOrUsername(String emailOrUsername) {
        this.emailOrUsername = emailOrUsername;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
package ut.edu.evcs.project_java.web.dto.auth;

public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresIn; // seconds
    private UserView user;

    public static class UserView {
        public String id;
        public String email;
        public String username;
        public String fullName;
        public String type;
    }

    // getters/setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
    public UserView getUser() { return user; }
    public void setUser(UserView user) { this.user = user; }
}

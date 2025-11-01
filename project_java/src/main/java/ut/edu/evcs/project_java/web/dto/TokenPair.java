package ut.edu.evcs.project_java.web.dto;

public class TokenPair {
    private String accessToken;

    public TokenPair() { }

    public TokenPair(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}

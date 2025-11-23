package ut.edu.evcs.project_java.web.dto.admin;

public class UserDetailDTO {
    private String id;
    private String email;
    private String username;
    private String fullName;
    private String phone;
    private String userType;
    private String currentSubscriptionPlan;
    private Double walletBalance;

    public UserDetailDTO() {
    }

    public UserDetailDTO(String id, String email, String username, String fullName, String phone,
            String userType, String currentSubscriptionPlan, Double walletBalance) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.fullName = fullName;
        this.phone = phone;
        this.userType = userType;
        this.currentSubscriptionPlan = currentSubscriptionPlan;
        this.walletBalance = walletBalance;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getCurrentSubscriptionPlan() {
        return currentSubscriptionPlan;
    }

    public void setCurrentSubscriptionPlan(String currentSubscriptionPlan) {
        this.currentSubscriptionPlan = currentSubscriptionPlan;
    }

    public Double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(Double walletBalance) {
        this.walletBalance = walletBalance;
    }
}

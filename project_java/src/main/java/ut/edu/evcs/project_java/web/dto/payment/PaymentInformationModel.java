package ut.edu.evcs.project_java.web.dto.payment;

public class PaymentInformationModel {
    private String orderType;
    private double amount;
    private String orderDescription;
    private String name;
    private String bankCode;

    public PaymentInformationModel() {
    }

    public PaymentInformationModel(String orderType, double amount, String orderDescription, String name,
            String bankCode) {
        this.orderType = orderType;
        this.amount = amount;
        this.orderDescription = orderDescription;
        this.name = name;
        this.bankCode = bankCode;
    }

    // --- GETTERS VÀ SETTERS ---

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
}
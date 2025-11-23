package ut.edu.evcs.project_java.web.dto.payment;

public class PaymentResponseModel {
    private String orderDescription;
    private String transactionId;
    private String orderId;
    private String paymentMethod;
    private String paymentId;
    private boolean success;
    private String token;
    private String vnPayResponseCode;

    public PaymentResponseModel() {
    }

    public PaymentResponseModel(String orderDescription, String transactionId, String orderId, String paymentMethod,
            String paymentId, boolean success, String token, String vnPayResponseCode) {
        this.orderDescription = orderDescription;
        this.transactionId = transactionId;
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.paymentId = paymentId;
        this.success = success;
        this.token = token;
        this.vnPayResponseCode = vnPayResponseCode;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVnPayResponseCode() {
        return vnPayResponseCode;
    }

    public void setVnPayResponseCode(String vnPayResponseCode) {
        this.vnPayResponseCode = vnPayResponseCode;
    }
}

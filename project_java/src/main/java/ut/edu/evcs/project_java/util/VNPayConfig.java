package ut.edu.evcs.project_java.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VNPayConfig {

    @Value("${vnpay.tmn-code}")
    public String vnp_TmnCode;

    @Value("${vnpay.secret-key}")
    public String vnp_HashSecret;

    @Value("${vnpay.pay-url}")
    public String vnp_PayUrl;

    @Value("${vnpay.return-url}")
    public String vnp_ReturnUrl;

    @Value("${vnpay.ipn-url}")
    public String vnp_IpnUrl;

    @Value("${vnpay.api-url}")
    public String vnp_ApiUrl;

    @Value("${vnpay.version:2.1.0}")
    public String vnp_Version;

    @Value("${vnpay.command.pay:pay}")
    public String vnp_Command_Pay;

    @Value("${vnpay.command.query:querydr}")
    public String vnp_Command_Query;

    @Value("${vnpay.command.refund:refund}")
    public String vnp_Command_Refund;

    // Constants
    public static final String VNP_CURRENCY = "VND";
    public static final String VNP_LOCALE_VN = "vn";
    public static final String VNP_LOCALE_EN = "en";

    public String getTmnCode() {
        return vnp_TmnCode;
    }

    public String getHashSecret() {
        return vnp_HashSecret;
    }

    public String getPayUrl() {
        return vnp_PayUrl;
    }

    public String getReturnUrl() {
        return vnp_ReturnUrl;
    }

    public String getIpnUrl() {
        return vnp_IpnUrl;
    }

    public String getApiUrl() {
        return vnp_ApiUrl;
    }

    public String getVersion() {
        return vnp_Version;
    }

    public String getCommandPay() {
        return vnp_Command_Pay;
    }

    public String getCommandQuery() {
        return vnp_Command_Query;
    }

    public String getCommandRefund() {
        return vnp_Command_Refund;
    }
}

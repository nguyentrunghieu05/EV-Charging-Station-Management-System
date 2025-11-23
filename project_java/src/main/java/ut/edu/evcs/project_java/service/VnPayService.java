package ut.edu.evcs.project_java.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import ut.edu.evcs.project_java.util.VnPayLibrary;
import ut.edu.evcs.project_java.web.dto.payment.PaymentInformationModel;
import ut.edu.evcs.project_java.web.dto.payment.PaymentResponseModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnPayService implements IVnPayService {

    @Value("${vnpay.tmn-code}")
    private String tmnCode;

    @Value("${vnpay.secret-key}")
    private String hashSecret;

    @Value("${vnpay.pay-url}")
    private String payUrl;

    @Value("${vnpay.return-url}")
    private String returnUrl;

    @Value("${vnpay.version:2.1.0}")
    private String version;

    @Value("${vnpay.command:pay}")
    private String command;

    @Value("${vnpay.currency:VND}")
    private String currCode;

    @Value("${vnpay.locale:vn}")
    private String locale;

    @Value("${vnpay.timezone:Asia/Ho_Chi_Minh}")
    private String timeZoneId;

    @Override
    public String createPaymentUrl(PaymentInformationModel model, HttpServletRequest request) {
        try {
            Map<String, String> vnp_Params = new HashMap<>();
            VnPayLibrary vnPayLib = new VnPayLibrary();

            String vnp_TxnRef = String.valueOf(System.currentTimeMillis());

            TimeZone tz = TimeZone.getTimeZone(timeZoneId);
            Calendar cld = Calendar.getInstance(tz);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());

            vnp_Params.put("vnp_Version", version);
            vnp_Params.put("vnp_Command", command);
            vnp_Params.put("vnp_TmnCode", tmnCode);

            long amount = (long) (model.getAmount() * 100);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));

            vnp_Params.put("vnp_CurrCode", currCode);
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", model.getOrderDescription());

            String orderType = (model.getOrderType() != null && !model.getOrderType().isEmpty()) ? model.getOrderType()
                    : "other";
            vnp_Params.put("vnp_OrderType", orderType);

            vnp_Params.put("vnp_Locale", locale);
            vnp_Params.put("vnp_ReturnUrl", returnUrl);

            String ipAddr = vnPayLib.getIpAddress(request);
            vnp_Params.put("vnp_IpAddr", ipAddr);

            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            if (model.getBankCode() != null && !model.getBankCode().isEmpty()) {
                vnp_Params.put("vnp_BankCode", model.getBankCode());
            }

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {

                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            String queryUrl = query.toString();

            String vnp_SecureHash = vnPayLib.hmacSHA512(hashSecret, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

            return payUrl + "?" + queryUrl;

        } catch (Exception e) {
            System.err.println("Error creating payment URL: " + e.getMessage());
            return "";
        }
    }

    @Override
    public PaymentResponseModel paymentExecute(MultiValueMap<String, String> queryParams) {
        try {
            VnPayLibrary vnPayLib = new VnPayLibrary();
            PaymentResponseModel response = new PaymentResponseModel();

            Map<String, String> fields = new HashMap<>();
            for (String key : queryParams.keySet()) {
                if (key.startsWith("vnp_")) {
                    fields.put(key, queryParams.getFirst(key));
                }
            }

            String vnp_SecureHash = queryParams.getFirst("vnp_SecureHash");

            if (fields.containsKey("vnp_SecureHashType")) {
                fields.remove("vnp_SecureHashType");
            }
            if (fields.containsKey("vnp_SecureHash")) {
                fields.remove("vnp_SecureHash");
            }

            List<String> fieldNames = new ArrayList<>(fields.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = fields.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        hashData.append('&');
                    }
                }
            }

            boolean checkSignature = false;
            if (vnp_SecureHash != null) {
                String expectedHash = vnPayLib.hmacSHA512(hashSecret, hashData.toString());
                checkSignature = expectedHash.equals(vnp_SecureHash);
            }

            if (!checkSignature) {
                response.setSuccess(false);
                response.setVnPayResponseCode("99");
                return response;
            }

            String vnpResponseCode = fields.get("vnp_ResponseCode");
            response.setSuccess("00".equals(vnpResponseCode));
            response.setPaymentMethod("VnPay");
            response.setOrderDescription(fields.get("vnp_OrderInfo"));
            response.setOrderId(fields.get("vnp_TxnRef"));
            response.setPaymentId(fields.get("vnp_TransactionNo"));
            response.setTransactionId(fields.get("vnp_TransactionNo"));
            response.setToken(vnp_SecureHash);
            response.setVnPayResponseCode(vnpResponseCode);

            return response;
        } catch (Exception e) {
            PaymentResponseModel response = new PaymentResponseModel();
            response.setSuccess(false);
            response.setVnPayResponseCode("99");
            return response;
        }
    }
}
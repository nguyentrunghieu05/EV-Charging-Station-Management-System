package ut.edu.evcs.project_java.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.MultiValueMap;
import ut.edu.evcs.project_java.web.dto.payment.PaymentInformationModel;
import ut.edu.evcs.project_java.web.dto.payment.PaymentResponseModel;

/**
 * VNPay Service Interface
 */
public interface IVnPayService {

    /**
     * Create VNPay payment URL
     * @param model Payment information
     * @param request HTTP request for getting IP address
     * @return VNPay payment URL
     */
    String createPaymentUrl(PaymentInformationModel model, HttpServletRequest request);

    /**
     * Execute payment - validate and process payment response
     * @param queryParams Response query parameters from VNPay
     * @return Payment response with transaction details
     */
    PaymentResponseModel paymentExecute(MultiValueMap<String, String> queryParams);
}

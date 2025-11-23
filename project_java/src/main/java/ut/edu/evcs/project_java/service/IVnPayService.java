package ut.edu.evcs.project_java.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.MultiValueMap;
import ut.edu.evcs.project_java.web.dto.payment.PaymentInformationModel;
import ut.edu.evcs.project_java.web.dto.payment.PaymentResponseModel;

public interface IVnPayService {

    String createPaymentUrl(PaymentInformationModel model, HttpServletRequest request);

    PaymentResponseModel paymentExecute(MultiValueMap<String, String> queryParams);
}

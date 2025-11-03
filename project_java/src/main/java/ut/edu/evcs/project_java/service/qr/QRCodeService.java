package ut.edu.evcs.project_java.service.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class QRCodeService {

    // Tạo mã QR cho connector
    public String generateQR(String connectorId) {
        try {
            String qrContent = "EVCS-CONNECTOR:" + connectorId;
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 250, 250);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] pngData = outputStream.toByteArray();

            // Trả về base64 string để hiển thị ảnh QR
            return Base64.getEncoder().encodeToString(pngData);

        } catch (WriterException | IOException e) {
            throw new RuntimeException("Không thể tạo mã QR", e);
        }
    }

    // Giải mã QR và bắt đầu session
    public String scanAndStart(String qrCode, Long driverId) {
        String decoded;
        try {
            decoded = new String(Base64.getDecoder().decode(qrCode));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Mã QR không hợp lệ");
        }

        if (!decoded.startsWith("EVCS-CONNECTOR:")) {
            throw new IllegalArgumentException("Mã QR không hợp lệ");
        }

        String connectorId = decoded.substring("EVCS-CONNECTOR:".length());
        // TODO: sau này thêm logic thật - kiểm tra connector, tạo session, lưu DB
        return "🔌 Bắt đầu session cho tài xế " + driverId + " tại connector " + connectorId;
    }
}

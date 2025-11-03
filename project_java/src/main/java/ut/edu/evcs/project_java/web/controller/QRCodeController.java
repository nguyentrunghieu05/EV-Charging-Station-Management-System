package ut.edu.evcs.project_java.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import ut.edu.evcs.project_java.service.qr.QRCodeService;

@RestController
@RequestMapping("/api/qr")
@Tag(name = "QR Code", description = "QR Code generation and session start")
public class QRCodeController {

    private final QRCodeService qrCodeService;

    // Constructor (thay cho @RequiredArgsConstructor của Lombok)
    public QRCodeController(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/generate/{connectorId}")
    @Operation(summary = "Generate QR code for a connector")
    public String generateQR(@PathVariable String connectorId) {
        return qrCodeService.generateQR(connectorId);
    }

    @PostMapping("/scan")
    @Operation(summary = "Scan QR code and start session")
    public String scanAndStart(
            @RequestParam String qrCode,
            @RequestParam Long driverId
    ) {
        return qrCodeService.scanAndStart(qrCode, driverId);
    }
}

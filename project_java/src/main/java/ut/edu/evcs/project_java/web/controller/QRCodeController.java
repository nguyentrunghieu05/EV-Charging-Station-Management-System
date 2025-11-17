package ut.edu.evcs.project_java.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ut.edu.evcs.project_java.service.qr.QRCodeService;

@RestController
@RequestMapping("/api/qr")
@Tag(name = "QR Code", description = "QR Code generation and session start")
@PreAuthorize("hasAnyRole('EV_DRIVER', 'CS_STAFF', 'ADMIN')")
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
            @RequestParam String driverId
    ) {
        if (driverId == null || driverId.isBlank()) {
            throw new IllegalArgumentException("driverId is required and must be valid");
        }
        return qrCodeService.scanAndStart(qrCode, driverId);
    }
}

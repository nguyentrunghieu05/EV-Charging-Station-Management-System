package ut.edu.evcs.project_java.web.controller;

import java.nio.file.Path;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ut.edu.evcs.project_java.domain.billing.Invoice;
import ut.edu.evcs.project_java.repo.InvoiceRepository;
import ut.edu.evcs.project_java.service.BillingService;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final BillingService billingService;
    private final InvoiceRepository invoiceRepo;

    public InvoiceController(BillingService billingService, InvoiceRepository invoiceRepo) {
        this.billingService = billingService;
        this.invoiceRepo = invoiceRepo;
    }

    @PostMapping
    public Invoice createInvoice(@RequestBody Map<String, String> body) {
        return billingService.createInvoice(body.get("sessionId"));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<FileSystemResource> downloadPdf(@PathVariable("id") String id) {
        Path p = Path.of("uploads/invoices/" + id + ".pdf");
        FileSystemResource resource = new FileSystemResource(p);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "inline; filename=\"" + id + ".pdf\"")
                .body(resource);
    }
}

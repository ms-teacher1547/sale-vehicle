package com.tpinf4067.sale_vehicle.Controller;

import com.tpinf4067.sale_vehicle.patterns.payment.*;
import com.tpinf4067.sale_vehicle.service.PaymentService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ✅ Création d’un paiement avec calcul des taxes
    @PostMapping("/")
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(request.getOrderId(), request.getPaymentType(), request.getCountry()));
    }
    

    // ✅ Confirmation d’un paiement
    @PutMapping("/{paymentId}/confirm")
    public ResponseEntity<Payment> confirmPayment(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.confirmPayment(paymentId));
    }

    // ✅ Rejet d’un paiement
    @PutMapping("/{paymentId}/reject")
    public ResponseEntity<Payment> rejectPayment(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.rejectPayment(paymentId));
    }

    // ✅ Téléchargement de la facture PDF
    @GetMapping("/invoice/{filename}")
    public ResponseEntity<Resource> downloadInvoice(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("documents").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

package com.tpinf4067.sale_vehicle.Controller;

import com.tpinf4067.sale_vehicle.patterns.auth.User;
import com.tpinf4067.sale_vehicle.patterns.document.Document;
import com.tpinf4067.sale_vehicle.patterns.payment.*;
import com.tpinf4067.sale_vehicle.repository.DocumentRepository;
import com.tpinf4067.sale_vehicle.service.PaymentService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final DocumentRepository documentRepository;

    public PaymentController(PaymentService paymentService, DocumentRepository documentRepository) {
        this.paymentService = paymentService;
        this.documentRepository = documentRepository;
    }

    // ✅ Création d’un paiement (récupération automatique de la commande en attente)
    @PostMapping("/")
    public ResponseEntity<Payment> createPayment(
            @RequestBody PaymentRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(paymentService.processPayment(user, request.getPaymentType(), request.getCountry()));
    }
    

    // ✅ Confirmation d’un paiement (récupération automatique du dernier paiement en attente)
    @PutMapping("/confirm")
    public ResponseEntity<Payment> confirmPayment(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(paymentService.confirmPayment(user));
    }

    // ✅ Rejet d’un paiement (utilisation automatique du dernier paiement en attente)
    @PutMapping("/reject")
    public ResponseEntity<Payment> rejectPayment(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(paymentService.rejectPayment(user));
    }

    // ✅ Récupérer toutes les factures pour ADMIN
    @GetMapping("/invoices")
    public ResponseEntity<List<Document>> getAllInvoices() {
        List<Document> invoices = documentRepository.findAll();
        return ResponseEntity.ok(invoices);
    }

    // ✅ Récupérer uniquement les factures du USER connecté
    @GetMapping("/my-invoices")
    public ResponseEntity<List<Document>> getUserInvoices(@AuthenticationPrincipal User user) {
        List<Document> invoices = documentRepository.findByPayment_Order_CustomerId(user.getCustomer().getId());
        return ResponseEntity.ok(invoices);
    }

    // ✅ Télécharger une facture par ID (au lieu du filename)
    @GetMapping("/invoice/download/{id}")
    public ResponseEntity<Resource> downloadInvoice(@PathVariable Long id) {
        Document invoice = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("❌ Facture introuvable"));

        try {
            Path filePath = Paths.get("documents").resolve(invoice.getFilename()).normalize();
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

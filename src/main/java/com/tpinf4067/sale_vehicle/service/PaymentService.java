package com.tpinf4067.sale_vehicle.service;

import com.tpinf4067.sale_vehicle.patterns.payment.*;
import com.tpinf4067.sale_vehicle.repository.OrderRepository;
import com.tpinf4067.sale_vehicle.patterns.document.*;
import com.tpinf4067.sale_vehicle.patterns.order.factory.Order;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PDFDocumentAdapter pdfAdapter;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.pdfAdapter = new PDFDocumentAdapter();
    }

    // ✅ Traiter un paiement avec calcul des taxes
    public Payment processPayment(Long orderId, PaymentType paymentType, String country) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée !"));

        // 🔥 Vérification si la commande est déjà payée
        if (paymentRepository.existsByOrderAndStatus(order, PaymentStatus.PAYE)) {
            throw new IllegalStateException("La commande a déjà été payée !");
        }

        // 🔥 Création du paiement avec calcul des taxes
        Payment payment = new Payment(order, paymentType, country);
        paymentRepository.save(payment);

        System.out.println("💳 Paiement créé pour la commande #" + orderId + " | Montant total : " + payment.getTotalAmount() + " €");

        return payment;
    }

    // ✅ Confirmation d’un paiement
    public Payment confirmPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Paiement non trouvé !"));

        if (payment.isPaid()) {
            throw new IllegalStateException("Le paiement a déjà été validé !");
        }

        payment.confirmPayment();
        paymentRepository.save(payment);

        // 🔥 Génération de la facture après confirmation
        generateInvoice(payment);

        System.out.println("✅ Paiement confirmé pour la commande #" + payment.getOrder().getId());
        return payment;
    }

    // ✅ Rejet d’un paiement
    public Payment rejectPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Paiement non trouvé !"));

        if (payment.isPaid()) {
            throw new IllegalStateException("Impossible de rejeter un paiement déjà validé !");
        }

        payment.rejectPayment();
        paymentRepository.save(payment);

        System.out.println("❌ Paiement rejeté pour la commande #" + payment.getOrder().getId());
        return payment;
    }

    // ✅ Génération de la facture PDF après validation du paiement
    private void generateInvoice(Payment payment) {
        Order order = payment.getOrder();

        // 🔥 Construction de la facture
        Document invoice = new Document();
        invoice.setTitle("📄 Facture de Paiement");

        String content = "<p><strong>Commande #" + order.getId() + "</strong></p>" +
                         "<p><strong>Client :</strong> " + order.getCustomer().getName() + "</p>" +
                         "<p><strong>Pays :</strong> " + payment.getCountry() + "</p>" +
                         "<p><strong>Montant HT :</strong> " + payment.getAmount() + " €</p>" +
                         "<p><strong>Taxes :</strong> " + payment.getTaxes() + " €</p>" +
                         "<p><strong>Total TTC :</strong> " + payment.getTotalAmount() + " €</p>";

        invoice.setContent(content);
        DocumentLiasseSingleton.getInstance().addDocument(invoice);

        // 🔥 Export en PDF
        pdfAdapter.export(invoice);

        System.out.println("📄 Facture générée et exportée en PDF.");
    }
}

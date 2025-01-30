package com.tpinf4067.sale_vehicle.service;

import com.tpinf4067.sale_vehicle.patterns.payment.*;
import com.tpinf4067.sale_vehicle.repository.OrderRepository;
import com.tpinf4067.sale_vehicle.repository.PaymentRepository;
import com.tpinf4067.sale_vehicle.patterns.document.*;
import com.tpinf4067.sale_vehicle.patterns.order.factory.Order;
import com.tpinf4067.sale_vehicle.patterns.payment.strategy.TaxStrategy;
import com.tpinf4067.sale_vehicle.patterns.payment.strategy.TaxStrategyFactory;
import com.tpinf4067.sale_vehicle.patterns.auth.User;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    // ✅ 🔥 Nouvelle méthode pour récupérer la dernière commande non payée
    private Order getLastUnpaidOrder(Long customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .filter(order -> !paymentRepository.existsByOrderAndStatus(order, PaymentStatus.PAYE))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("❌ Aucune commande en attente de paiement."));
    }

    // ✅ Modifier la méthode `processPayment` pour ne plus exiger `orderId`
    public Payment processPayment(User user, PaymentType paymentType, String country) {
        if (user.getCustomer() == null) {
            throw new IllegalStateException("❌ Impossible de traiter le paiement. Aucun client associé.");
        }

        // 🔥 Récupérer la dernière commande en attente de paiement
        Order order = getLastUnpaidOrder(user.getCustomer().getId());

        // 🔥 Utilisation du Pattern Strategy pour le calcul des taxes
        TaxStrategy taxStrategy = TaxStrategyFactory.getTaxStrategy(country);
        double taxes = taxStrategy.calculateTax(order.getTotalPrice());
        double totalAmount = order.getTotalPrice() + taxes;

        // 🔥 Création du paiement
        Payment payment = new Payment(order, paymentType, country, order.getTotalPrice(), taxes, totalAmount);
        paymentRepository.save(payment);

        System.out.println("💳 Paiement créé pour la commande #" + order.getId() + " | Montant total : " + payment.getTotalAmount() + " FCFA");

        return payment;
    }

    // ✅ Modifier la confirmation de paiement pour ne plus exiger `paymentId`
    public Payment confirmPayment(User user) {
        if (user.getCustomer() == null) {
            throw new IllegalStateException("❌ Aucun client associé à cet utilisateur.");
        }

        // 🔥 Récupérer le dernier paiement en attente
        Payment payment = paymentRepository.findByOrder_CustomerId(user.getCustomer().getId())
                .stream()
                .filter(p -> !p.isPaid())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("❌ Aucun paiement en attente de confirmation."));

        // 🔥 Vérification et confirmation
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

    // ✅ Rejet d’un paiement sans paymentId (récupération automatique du dernier paiement en attente)
    public Payment rejectPayment(User user) {
        Payment payment = paymentRepository.findFirstByOrderCustomerIdAndStatus(user.getCustomer().getId(), PaymentStatus.EN_ATTENTE)
                .orElseThrow(() -> new IllegalArgumentException("Aucun paiement en attente trouvé pour ce client !"));
    
        payment.rejectPayment();
        paymentRepository.save(payment);
    
        System.out.println("❌ Paiement rejeté pour la commande #" + payment.getOrder().getId());
        return payment;
    }

    // ✅ Génération de la facture PDF après validation du paiement avec un nom unique
    private void generateInvoice(Payment payment) {
        Order order = payment.getOrder();

        // 🔥 Génération d'un nom unique pour la facture
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fileName = "Facture_" + order.getCustomer().getName().replace(" ", "_") + "_" + dateFormat.format(new Date()) + ".pdf";

        // 🔥 Construction de la facture
        Document invoice = new Document();
        invoice.setTitle("Facture de Paiement");
        invoice.setFilename(fileName); // ✅ Utilisation du nom unique

        String content = "<p><strong>Commande #" + order.getId() + "</strong></p>" +
                         "<p><strong>Client :</strong> " + order.getCustomer().getName() + "</p>" +
                         "<p><strong>Pays :</strong> " + payment.getCountry() + "</p>" +
                         "<p><strong>Montant HT :</strong> " + payment.getAmount() + " FCFA</p>" +
                         "<p><strong>Taxes :</strong> " + payment.getTaxes() + " FCFA</p>" +
                         "<p><strong>Total TTC :</strong> " + payment.getTotalAmount() + " FCFA</p>";

        invoice.setContent(content);
        DocumentLiasseSingleton.getInstance().addDocument(invoice);

        // 🔥 Export en PDF
        pdfAdapter.export(invoice);

        System.out.println("📄 Facture générée et exportée sous le nom : " + fileName);
    }
}

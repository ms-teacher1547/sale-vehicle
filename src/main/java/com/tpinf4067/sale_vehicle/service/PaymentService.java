package com.tpinf4067.sale_vehicle.service;

import com.tpinf4067.sale_vehicle.patterns.payment.*;
import com.tpinf4067.sale_vehicle.patterns.payment.template.TaxTemplate;
import com.tpinf4067.sale_vehicle.patterns.payment.template.TaxTemplateFactory;
import com.tpinf4067.sale_vehicle.repository.DocumentRepository;
import com.tpinf4067.sale_vehicle.repository.OrderRepository;
import com.tpinf4067.sale_vehicle.repository.PaymentRepository;
import com.tpinf4067.sale_vehicle.patterns.document.*;
import com.tpinf4067.sale_vehicle.patterns.order.factory.Order;
import com.tpinf4067.sale_vehicle.patterns.auth.User;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final DocumentRepository documentRepository;  // ✅ Ajout du repository des documents
    private final PDFDocumentAdapter pdfAdapter;
    private final OrderService orderService;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository, DocumentRepository documentRepository, OrderService orderService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.documentRepository = documentRepository;
        this.orderService = orderService;
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
        TaxTemplate taxStrategy = TaxTemplateFactory.getTaxStrategy(country);
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
        paymentRepository.flush(); // 🔥 Forcer l'enregistrement immédiat en BD
    
        // 🔥 Génération de la facture après confirmation
        generateInvoice(payment);

        // ✅ Mettre à jour l'état de la commande après confirmation du paiement
        Order order = payment.getOrder();
        if (!"LIVREE".equals(order.getState().getStatus())) {
            orderService.changeOrderStatus(order.getId(), true); // 🔥 Passer la commande à "LIVREE"
        }
    
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
    // ✅ Génération et enregistrement de la facture après confirmation du paiement
    // ✅ Génération et enregistrement de la facture après confirmation du paiement
    private void generateInvoice(Payment payment) {
        Order order = payment.getOrder();

        // ✅ Génération d'un nom unique pour la facture liée à la commande
        String fileName = "Facture_de_Paiement_-_Commande_#" + order.getId() + ".pdf";

        // ✅ Construction de la facture
        Document invoice = new Document();
        invoice.setTitle("Facture de Paiement - Commande #" + order.getId());
        invoice.setFilename(fileName); // ✅ Utilisation du nom unique
        invoice.setContent("<p><strong>Commande #" + order.getId() + "</strong></p>" +
                        "<p><strong>Client :</strong> " + order.getCustomer().getName() + "</p>" +
                        "<p><strong>Pays :</strong> " + payment.getCountry() + "</p>" +
                        "<p><strong>Montant HT :</strong> " + payment.getAmount() + " FCFA</p>" +
                        "<p><strong>Taxes :</strong> " + payment.getTaxes() + " FCFA</p>" +
                        "<p><strong>Total TTC :</strong> " + payment.getTotalAmount() + " FCFA</p>");

        // ✅ Enregistrer la facture en base de données
        invoice.setPayment(payment); // Associer la facture au paiement
        documentRepository.save(invoice); // Sauvegarde dans la BD

        // ✅ Export en PDF
        pdfAdapter.export(invoice);

        System.out.println("📄 Facture enregistrée en BD et générée sous le nom : " + fileName);
    }



    // ✅ Récupérer toutes les factures pour ADMIN
   public List<PaymentInvoiceDTO> getAllInvoices() {
        List<Document> invoices = documentRepository.findAll();
        return invoices.stream().map(doc -> new PaymentInvoiceDTO(
            doc.getId(),
            doc.getTitle(),
            doc.getFilename(),
            doc.getContent(),
            doc.getOrder() != null ? doc.getOrder().getId() : null,
            doc.getPayment() != null ? doc.getPayment().getId() : null
        )).collect(Collectors.toList());
    }


    // ✅ Récupérer uniquement les factures d’un utilisateur
    public List<Document> getUserInvoices(User user) {
        return documentRepository.findByPayment_Order_CustomerId(user.getCustomer().getId());
    }
    

    // ✅ Récupérer une facture spécifique par ID
    public Document getInvoiceById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("❌ Facture introuvable avec ID : " + id));
    }
}

package com.tpinf4067.sale_vehicle.patterns.payment;

import com.tpinf4067.sale_vehicle.patterns.order.Order;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Order order;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.EN_ATTENTE; // Par défaut, en attente

    private String country; // Pays de paiement pour la gestion des taxes

    private double amount; // Montant initial sans taxes
    private double taxes; // Montant des taxes
    private double totalAmount; // Montant total avec taxes

    // ✅ Constructeur prenant en compte le calcul des taxes
    public Payment(Order order, PaymentType paymentType, String country) {
        this.order = order;
        this.paymentType = paymentType;
        this.country = country;
        this.amount = order.getTotalPrice(); // 🛒 Récupération du prix total de la commande
        this.taxes = calculateTaxes(country, amount);
        this.totalAmount = this.amount + this.taxes;
        this.status = PaymentStatus.EN_ATTENTE;
    }

    // ✅ Méthode pour calculer les taxes selon le pays
    /**
     * Calculates the taxes based on the given country and amount.
     *
     * @param country the country for which the tax is to be calculated. 
     *                Supported countries are "FRANCE", "SENEGAL", "GABON", and "TCHAD".
     * @param amount the amount on which the tax is to be calculated.
     * @return the calculated tax amount. 
     *         - For "FRANCE", the tax rate is 20%.
     *         - For "SENEGAL", the tax rate is 15%.
     *         - For "GABON", the tax rate is 7%.
     *         - For "TCHAD", the tax rate is 15%.
     *         - For any other country, a default tax rate of 10% is applied.
     */
    private double calculateTaxes(String country, double amount) {
        return switch (country.toUpperCase()) {
            case "FRANCE" -> amount * 0.20;  // 🇫🇷 TVA 20%
            case "SENEGAL" -> amount * 0.15;   // 🇸🇳 TVA 15%
            case "GABON" -> amount * 0.07; // 🇬🇦 TVA 7%
            case "TCHAD" -> amount * 0.15; // 🇹🇩 TVA 15%
            default -> amount * 0.10;       // 🌍 Par défaut, taxe de 10%
        };
    }

    // ✅ Vérification si le paiement est déjà payé
    public boolean isPaid() {
        return this.status == PaymentStatus.PAYE;
    }

    // ✅ Validation du paiement
    public void confirmPayment() {
        if (!isPaid()) {
            this.status = PaymentStatus.PAYE;
        }
    }

    // ✅ Rejet du paiement
    public void rejectPayment() {
        if (!isPaid()) {
            this.status = PaymentStatus.REFUSE;
        }
    }
}

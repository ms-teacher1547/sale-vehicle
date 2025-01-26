package com.tpinf4067.sale_vehicle.patterns.payment;

import com.tpinf4067.sale_vehicle.patterns.order.factory.Order;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
    private PaymentStatus status = PaymentStatus.EN_ATTENTE;

    private String country;
    private BigDecimal amount;
    private BigDecimal taxes;
    private BigDecimal totalAmount;

    // ✅ Nouveau constructeur avec BigDecimal pour éviter les erreurs de précision
    public Payment(Order order, PaymentType paymentType, String country, double amount, double taxes, double totalAmount) {
        this.order = order;
        this.paymentType = paymentType;
        this.country = country;
        this.amount = formatPrice(amount);
        this.taxes = formatPrice(taxes);
        this.totalAmount = formatPrice(totalAmount);
        this.status = PaymentStatus.EN_ATTENTE;
    }

    // ✅ Méthode pour formater les montants sans notation scientifique
    private BigDecimal formatPrice(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isPaid() {
        return this.status == PaymentStatus.PAYE;
    }

    public void confirmPayment() {
        if (!isPaid()) {
            this.status = PaymentStatus.PAYE;
        }
    }

    public void rejectPayment() {
        if (!isPaid()) {
            this.status = PaymentStatus.REFUSE;
        }
    }
}

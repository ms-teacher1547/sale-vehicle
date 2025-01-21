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
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType; // COMPTANT ou CREDIT

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus; // EN_ATTENTE, PAYE, REFUSE

    private double amount;
    private double tax;
    private String country; // Pays pour calculer la taxe
}

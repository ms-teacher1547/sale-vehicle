package com.tpinf4067.sale_vehicle.patterns.document;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.tpinf4067.sale_vehicle.patterns.order.factory.Order;
import com.tpinf4067.sale_vehicle.patterns.payment.Payment;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity // ✅ Indique que cette classe est une entité JPA
@Table(name = "documents") // ✅ Spécifie le nom de la table
@Getter
@Setter
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Génération auto de l'ID
    private Long id;

    private String title;
    private String filename;

    @Lob  // ✅ Permet de stocker un texte long
    @Column(columnDefinition = "TEXT")  // ✅ Définit le type SQL comme TEXT
    private String content;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = true) // 🔥 Associe la facture à un paiement
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY) // ✅ Plusieurs documents peuvent être associés à une commande
    @JoinColumn(name = "order_id") // ✅ Clé étrangère vers Order
    @JsonBackReference  // ✅ Empêche la récursion infinie
    private Order order;

    public void showDocument() {
        System.out.println(title + " : " + content);
    }
}

package com.tpinf4067.sale_vehicle.service.order;

import com.tpinf4067.sale_vehicle.domain.Vehicle;
import com.tpinf4067.sale_vehicle.service.customer.Customer;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Vehicle vehicle;

    @ManyToOne
    private Customer customer;

    // Stocker l'état en base sous forme de String
    private String state;

    public Order(Vehicle vehicle, Customer customer) {
        this.vehicle = vehicle;
        this.customer = customer;
        this.state = "EN_COURS"; // Par défaut, une nouvelle commande est "En cours"
    }

    // 🔥 Assurer que `state` est toujours défini après chargement depuis la base
    @PostLoad
    private void initState() {
        if (this.state == null) {
            this.state = "EN_COURS";
        }
    }

    // Méthodes pour changer d'état
    public void nextState() {
        if ("EN_COURS".equals(state)) {
            state = "VALIDEE";
        } else if ("VALIDEE".equals(state)) {
            state = "LIVREE";
        } else {
            System.out.println("❌ La commande est déjà livrée, elle ne peut plus avancer.");
        }
    }

    public void previousState() {
        if ("LIVREE".equals(state)) {
            state = "VALIDEE";
        } else if ("VALIDEE".equals(state)) {
            state = "EN_COURS";
        } else {
            System.out.println("❌ La commande est déjà au premier état (En cours).");
        }
    }

    public String getStatus() {
        return switch (state) {
            case "VALIDEE" -> "Validée";
            case "LIVREE" -> "Livrée";
            default -> "En cours";
        };
    }
}

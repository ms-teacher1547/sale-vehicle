package com.tpinf4067.sale_vehicle.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id") // 🔥 Association avec le panier
    @JsonBackReference
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "vehicle_id") // 🔥 Association avec un véhicule
    private Vehicle vehicle;

    private int quantity;

    // 🔥 Ajout de la relation avec les options du véhicule
    @ManyToMany
    @JoinTable(
        name = "cart_item_options",
        joinColumns = @JoinColumn(name = "cart_item_id"),
        inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private List<Option> options;
}

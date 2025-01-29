package com.tpinf4067.sale_vehicle.patterns.customer;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tpinf4067.sale_vehicle.patterns.auth.User;
import com.tpinf4067.sale_vehicle.patterns.customer.enums.CustomerType;
import com.tpinf4067.sale_vehicle.patterns.order.factory.Order;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String address;

    @OneToMany(mappedBy = "customer")
    @JsonManagedReference // Empêche la récursion infinie
    private List<Order> orders = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private CustomerType type;

    // 📌 Ajout pour gérer les filiales (Composite Pattern)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_company_id")
    private List<Customer> subsidiaries = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    @JsonBackReference // 🔥 Evite la boucle infinie dans la sérialisation JSON
    private User user;

    // ✅ Ajout d'un constructeur avec un User
    public Customer(String name, String email, String address, CustomerType type, User user) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.type = type;
        this.user = user;
    }

    // ✅ Ajout d'un constructeur prenant uniquement l'ID
    public Customer(Long id) {
        this.id = id;
    }

}

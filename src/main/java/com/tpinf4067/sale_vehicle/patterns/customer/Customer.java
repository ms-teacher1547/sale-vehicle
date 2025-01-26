package com.tpinf4067.sale_vehicle.patterns.customer;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonManagedReference // Empeche la recursion infinie
    private List<Order> orders = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private CustomerType type;

    // 📌 Ajout pour gérer les filiales (Composite Pattern)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_company_id")
    private List<Customer> subsidiaries;

    // ✅ Ajout d'un constructeur avec uniquement l'ID
    public Customer(Long id) {
        this.id = id;
    }

    // ✅ Ajout d'un constructeur avec uniquement le nom
    public void addSubsidiary(Customer subsidiary) {
        if (this.type != CustomerType.COMPANY) {
            throw new IllegalStateException("Seuls les clients de type COMPANY peuvent avoir des filiales.");
        }
        this.subsidiaries.add(subsidiary);
    }
   
    
}

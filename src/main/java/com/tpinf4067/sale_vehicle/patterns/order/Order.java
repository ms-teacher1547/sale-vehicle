package com.tpinf4067.sale_vehicle.patterns.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tpinf4067.sale_vehicle.domain.Option;
import com.tpinf4067.sale_vehicle.domain.Vehicle;
import com.tpinf4067.sale_vehicle.patterns.customer.Customer;

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
    private Customer customer;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date dateDeCommande;

    @ManyToMany
    @JoinTable(
        name = "order_vehicles",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "vehicle_id")
    )
    private List<Vehicle> vehicles = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "order_options",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private List<Option> options = new ArrayList<>();

    @Column(nullable = false)
    private String state = "EN_COURS"; // 🔥 Stocker l'état en base sous forme de String

    @PrePersist
    protected void onCreate() {
        this.dateDeCommande = new Date();
    }

    // 🔥 Assurer que `state` est toujours défini après chargement depuis la base
    @PostLoad
    private void initState() {
        if (this.state == null) {
            this.state = "EN_COURS";
        }
    }

    // ✅ Ajouter plusieurs véhicules avec options
    public void addVehicleWithOptions(Vehicle vehicle, List<Option> vehicleOptions) {
        this.vehicles.add(vehicle);
        this.options.addAll(vehicleOptions);
    }

    // 
    public Vehicle getVehicle() {
        return vehicles.isEmpty() ? null : vehicles.get(0);
    }
    

    // ✅ Récupérer le prix total (avec options)
    public double getTotalPrice() {
        double vehiclesPrice = vehicles.stream().mapToDouble(Vehicle::getPrice).sum();
        double optionsPrice = options.stream().mapToDouble(Option::getPrice).sum();
        return vehiclesPrice + optionsPrice;
    }

    // ✅ Gestion des états
    public void nextState() {
        switch (state) {
            case "EN_COURS" -> state = "VALIDEE";
            case "VALIDEE" -> state = "LIVREE";
            default -> System.out.println("❌ La commande est déjà livrée, elle ne peut plus avancer.");
        }
    }

    public void previousState() {
        switch (state) {
            case "LIVREE" -> state = "VALIDEE";
            case "VALIDEE" -> state = "EN_COURS";
            default -> System.out.println("❌ La commande est déjà au premier état (En cours).");
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

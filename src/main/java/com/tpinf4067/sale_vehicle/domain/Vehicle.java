package com.tpinf4067.sale_vehicle.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "vehicle_type", discriminatorType = DiscriminatorType.STRING)

public abstract class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    private String name;
    private Double price;

    private int stockQuantity; // Stock disponible
    private int yearOfManufacture; // Annee de fabrication
    private String fuelType;  // Essence, Diesel, Electrique, Hybride
    private int mileage; // Kilometrage (si applicable)

    public Vehicle(String name, double price, int stockQuantity, int yearOfManufacture, String fuelType, int mileage) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.yearOfManufacture = yearOfManufacture;
        this.fuelType = fuelType;
        this.mileage = mileage;
    }

    // Ajout du champ pour l'animation
    private String animationUrl;

    // Ajout du champ pour la date d'ajout
    @Column(name = "date_ajout", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAjout;

    // Méthode pour la date d'ajout
    @PrePersist
    protected void onCreate() {
        this.dateAjout = new Date();
    }

    // 
    public boolean isAvailable() {
        return stockQuantity > 0;
    }
    
    // 
    public void decreaseStock() {
        if (stockQuantity > 0) {
            stockQuantity--;
        } else {
            throw new IllegalStateException("Stock insuffisant pour ce véhicule.");
        }
    } 

}

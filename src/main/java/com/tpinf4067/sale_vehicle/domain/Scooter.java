package com.tpinf4067.sale_vehicle.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("SCOOTER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Scooter extends Vehicle {

    private boolean hasStorageBox; // Présence de coffre de rangement

    public Scooter(String name, double price, boolean hasStorageBox, int stockQuantity, int yearOfManufacture, String fuelType, int mileage) {
        super(name, price, stockQuantity, yearOfManufacture, fuelType, mileage);
        this.hasStorageBox = hasStorageBox;
    }
}

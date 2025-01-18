package com.tpinf4067.sale_vehicle.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("CAR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Car extends Vehicle{

    private int numberOfDoors;

    public Car(String name, double price, int numberOfDoors) {
        super(name, price);
        this.numberOfDoors = numberOfDoors;
    }
}

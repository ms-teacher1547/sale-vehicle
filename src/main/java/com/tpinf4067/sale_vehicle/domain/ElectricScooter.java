package com.tpinf4067.sale_vehicle.domain;

public class ElectricScooter extends Scooter{
    
    public ElectricScooter(String name, double price, boolean hasStorageBox, int stockQuantity, int yearOfManufacture, String fuelType, int mileage){
        super(name, price, hasStorageBox,stockQuantity, yearOfManufacture, fuelType, mileage);
    }
}

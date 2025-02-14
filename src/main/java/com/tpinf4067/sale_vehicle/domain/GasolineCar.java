package com.tpinf4067.sale_vehicle.domain;

public class GasolineCar extends Car{
   
    public GasolineCar(String name, double price, int numberOfDoors, int stockQuantity, int yearOfManufacture, String fuelType, int mileage){
        super(name, price,numberOfDoors, stockQuantity, yearOfManufacture,  fuelType, mileage);
    }
}

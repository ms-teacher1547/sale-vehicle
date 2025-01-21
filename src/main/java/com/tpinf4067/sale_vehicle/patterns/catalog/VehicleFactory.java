package com.tpinf4067.sale_vehicle.patterns.catalog;

import com.tpinf4067.sale_vehicle.domain.Vehicle;

public interface VehicleFactory {

    Vehicle createCar(String name, double price, int numberOfDoors);
    Vehicle createScooter(String name, double price, boolean hasStorageBox);
} 

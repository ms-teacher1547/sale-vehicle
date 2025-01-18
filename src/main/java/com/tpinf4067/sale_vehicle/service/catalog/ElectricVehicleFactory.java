package com.tpinf4067.sale_vehicle.service.catalog;

import com.tpinf4067.sale_vehicle.domain.Car;
import com.tpinf4067.sale_vehicle.domain.Scooter;
import com.tpinf4067.sale_vehicle.domain.Vehicle;

public class ElectricVehicleFactory implements VehicleFactory {
    @Override
    public Vehicle createCar(String name, double price, int numberOfDoors) {
        return new Car(name + " Electric", price, numberOfDoors);
    }

    @Override
    public Vehicle createScooter(String name, double price, boolean hasStorageBox) {
        return new Scooter(name + " Electric", price, hasStorageBox);
    }
}

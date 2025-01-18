package com.tpinf4067.sale_vehicle.service.catalog.decorator;

import com.tpinf4067.sale_vehicle.domain.Vehicle;

public class BasicVehicleDisplay implements VehicleDisplay {
    final Vehicle vehicle;

    public BasicVehicleDisplay(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public String display() {
        return "Véhicule : " + vehicle.getName() + " | Prix : " + vehicle.getPrice();
    }
}

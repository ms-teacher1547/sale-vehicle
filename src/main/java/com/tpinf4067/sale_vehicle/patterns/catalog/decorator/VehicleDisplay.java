package com.tpinf4067.sale_vehicle.patterns.catalog.decorator;

import com.tpinf4067.sale_vehicle.domain.Vehicle;

public interface VehicleDisplay {
    String display();
    Vehicle getVehicle(); // Ajoutez cette méthode
}

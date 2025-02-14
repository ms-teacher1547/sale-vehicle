package com.tpinf4067.sale_vehicle.patterns.iterator;

import com.tpinf4067.sale_vehicle.domain.Vehicle;

public interface VehiculeCollection {
    void addVehicule(Vehicle vehicule);
    Iterator createIterator();
}
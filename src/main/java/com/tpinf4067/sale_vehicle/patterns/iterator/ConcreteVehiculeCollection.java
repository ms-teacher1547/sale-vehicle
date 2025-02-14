package com.tpinf4067.sale_vehicle.patterns.iterator;

import java.util.ArrayList;
import java.util.List;

import com.tpinf4067.sale_vehicle.domain.Vehicle;

public class ConcreteVehiculeCollection implements VehiculeCollection {
    private List<Vehicle> vehicules;

    public ConcreteVehiculeCollection() {
        vehicules = new ArrayList<>();
    }

    @Override
    public void addVehicule(Vehicle vehicule) {
        vehicules.add(vehicule);
    }

    @Override
    public Iterator createIterator() {
        return new VehiculeIterator(vehicules);
    }
}
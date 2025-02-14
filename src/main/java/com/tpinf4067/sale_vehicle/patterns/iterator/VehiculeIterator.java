package com.tpinf4067.sale_vehicle.patterns.iterator;

import java.util.List;

import com.tpinf4067.sale_vehicle.domain.Vehicle;

public class VehiculeIterator implements Iterator {
    private List<Vehicle> vehicules;
    private int position = 0;

    public VehiculeIterator(List<Vehicle> vehicules) {
        this.vehicules = vehicules;
    }

    @Override
    public boolean hasNext() {
        return position < vehicules.size();
    }

    @Override
    public Object next() {
        if (this.hasNext()) {
            return vehicules.get(position++);
        }
        return null;
    }
}

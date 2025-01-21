package com.tpinf4067.sale_vehicle.patterns.catalog.decorator;

public abstract class VehicleDecorator implements VehicleDisplay {
    protected VehicleDisplay decoratedVehicle;

    public VehicleDecorator(VehicleDisplay decoratedVehicle) {
        this.decoratedVehicle = decoratedVehicle;
    }

    @Override
    public String display() {
        return decoratedVehicle.display();
    }
}

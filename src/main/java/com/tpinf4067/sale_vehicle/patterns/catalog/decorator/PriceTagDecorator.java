package com.tpinf4067.sale_vehicle.patterns.catalog.decorator;

public class PriceTagDecorator extends VehicleDecorator {
    public PriceTagDecorator(VehicleDisplay decoratedVehicle) {
        super(decoratedVehicle);
    }

    @Override
    public String display() {
        return decoratedVehicle.display() + " 💰 [PRIX FORMATÉ]";
    }
}

package com.tpinf4067.sale_vehicle.patterns.catalog.decorator;

import com.tpinf4067.sale_vehicle.domain.Vehicle;

public class PriceTagDecorator extends VehicleDecorator {
    public PriceTagDecorator(VehicleDisplay decoratedVehicle) {
        super(decoratedVehicle);
    }

    @Override
    public String display() {
        Vehicle vehicle = getVehicle(); // Utilisez getVehicle() au lieu de caster
        String formattedPrice = String.format("%.2f FCFA", vehicle.getPrice());
        return decoratedVehicle.display() + " 💰 [PRIX : " + formattedPrice + "]";
    }

    @Override
    public Vehicle getVehicle() {
        return decoratedVehicle.getVehicle(); // Retourne le véhicule du décorateur parent
    }
}

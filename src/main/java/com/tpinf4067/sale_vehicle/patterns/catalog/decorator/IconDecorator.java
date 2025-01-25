package com.tpinf4067.sale_vehicle.patterns.catalog.decorator;

import com.tpinf4067.sale_vehicle.domain.Car;
import com.tpinf4067.sale_vehicle.domain.Scooter;
import com.tpinf4067.sale_vehicle.domain.Vehicle;

public class IconDecorator extends VehicleDecorator {
    public IconDecorator(VehicleDisplay decoratedVehicle) {
        super(decoratedVehicle);
    }

    @Override
    public String display() {
        String icon = "";
        Vehicle vehicle = getVehicle(); // Utilisez getVehicle() au lieu de caster
        if (vehicle instanceof Car) {
            icon = "🚗";
        } else if (vehicle instanceof Scooter) {
            icon = "🛵";
        }
        return icon + " " + decoratedVehicle.display();
    }

    @Override
    public Vehicle getVehicle() {
        return decoratedVehicle.getVehicle(); // Retourne le véhicule du décorateur parent
    }
}

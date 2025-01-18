package com.tpinf4067.sale_vehicle.service.catalog.decorator;

import com.tpinf4067.sale_vehicle.domain.Car;
import com.tpinf4067.sale_vehicle.domain.Scooter;

public class IconDecorator extends VehicleDecorator {
    public IconDecorator(VehicleDisplay decoratedVehicle) {
        super(decoratedVehicle);
    }

    @Override
    public String display() {
        String icon = "";
        if (decoratedVehicle instanceof BasicVehicleDisplay) {
            if (((BasicVehicleDisplay) decoratedVehicle).vehicle instanceof Car) {
                icon = "🚗";
            } else if (((BasicVehicleDisplay) decoratedVehicle).vehicle instanceof Scooter) {
                icon = "🛵";
            }
        }
        return icon + " " + decoratedVehicle.display();
    }
}

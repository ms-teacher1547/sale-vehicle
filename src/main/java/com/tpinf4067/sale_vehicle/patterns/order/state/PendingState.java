package com.tpinf4067.sale_vehicle.patterns.order.state;

import com.tpinf4067.sale_vehicle.patterns.order.factory.Order;

public class PendingState implements OrderState {

    @Override
    public void next(Order order) {
        order.setState(new ValidatedState());
    }

    @Override
    public void previous(Order order) {
        System.out.println("❌ La commande est déjà à l'état initial (En attente).");
    }

    @Override
    public String getStatus() {
        return "EN_COURS";
    }
}

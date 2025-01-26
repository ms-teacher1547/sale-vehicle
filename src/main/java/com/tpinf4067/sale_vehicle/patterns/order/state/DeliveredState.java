package com.tpinf4067.sale_vehicle.patterns.order.state;

import com.tpinf4067.sale_vehicle.patterns.order.factory.Order;

public class DeliveredState implements OrderState {

    @Override
    public void next(Order order) {
        System.out.println("❌ La commande est déjà livrée, elle ne peut plus avancer.");
    }

    @Override
    public void previous(Order order) {
        order.setState(new ValidatedState());
    }

    @Override
    public String getStatus() {
        return "LIVREE";
    }
}

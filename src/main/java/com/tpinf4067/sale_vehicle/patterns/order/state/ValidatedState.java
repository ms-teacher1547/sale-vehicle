package com.tpinf4067.sale_vehicle.patterns.order.state;

import com.tpinf4067.sale_vehicle.patterns.order.factory.Order;

public class ValidatedState implements OrderState {

    @Override
    public void next(Order order) {
        order.setState(new DeliveredState());
    }

    @Override
    public void previous(Order order) {
        order.setState(new PendingState());
    }

    @Override
    public String getStatus() {
        return "VALIDEE";
    }
}

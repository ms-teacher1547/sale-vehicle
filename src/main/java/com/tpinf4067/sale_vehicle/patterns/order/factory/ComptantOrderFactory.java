package com.tpinf4067.sale_vehicle.patterns.order.factory;

import com.tpinf4067.sale_vehicle.domain.Cart;
import com.tpinf4067.sale_vehicle.patterns.order.Order;

public class ComptantOrderFactory implements OrderFactory {
    @Override
    public Order createOrder(Cart cart) {
        return new Order();
    }
}

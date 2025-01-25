package com.tpinf4067.sale_vehicle.patterns.order.factory;

import com.tpinf4067.sale_vehicle.domain.Cart;
import com.tpinf4067.sale_vehicle.patterns.order.Order;

public interface OrderFactory {
    Order createOrder(Cart cart);
}

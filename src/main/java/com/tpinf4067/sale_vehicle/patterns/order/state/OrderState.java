package com.tpinf4067.sale_vehicle.patterns.order.state;

import com.tpinf4067.sale_vehicle.patterns.order.factory.Order;

public interface OrderState {
    void next(Order order);
    void previous(Order order);
    String getStatus();
}

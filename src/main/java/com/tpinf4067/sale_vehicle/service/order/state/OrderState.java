package com.tpinf4067.sale_vehicle.service.order.state;

import com.tpinf4067.sale_vehicle.service.order.Order;

public interface OrderState {
    void next(Order order);
    void previous(Order order);
    String getStatus();
}

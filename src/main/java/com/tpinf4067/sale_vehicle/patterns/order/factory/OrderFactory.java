package com.tpinf4067.sale_vehicle.patterns.order.factory;

import com.tpinf4067.sale_vehicle.domain.Cart;
import com.tpinf4067.sale_vehicle.patterns.order.state.PendingState;

public interface OrderFactory {
    default Order createOrder(Cart cart){//, PaymentType paymentType) {
        Order order = new Order();
        order.setState(new PendingState()); // ✅ Initialisation correcte du State Pattern
        //order.setPaymentType(paymentType);
        return order;
    }
}

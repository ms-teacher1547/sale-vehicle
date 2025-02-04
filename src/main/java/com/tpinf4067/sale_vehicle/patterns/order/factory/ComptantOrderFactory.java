package com.tpinf4067.sale_vehicle.patterns.order.factory;

import com.tpinf4067.sale_vehicle.domain.Cart;
import com.tpinf4067.sale_vehicle.patterns.order.state.PendingState;

public class ComptantOrderFactory implements OrderFactory {
    @Override
    public Order createOrder(Cart cart){//, PaymentType paymentType) {
        // if (paymentType != PaymentType.COMPTANT) {
        //     throw new IllegalArgumentException("Cette factory ne gère que les paiements au comptant.");
        // }
        Order order = new Order();
        order.setState(new PendingState()); // ✅ Correct : État initial
        //order.setPaymentType(paymentType);
        return order;
    }
}

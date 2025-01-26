package com.tpinf4067.sale_vehicle.patterns.order.factory;

import com.tpinf4067.sale_vehicle.domain.Cart;
import com.tpinf4067.sale_vehicle.patterns.order.state.PendingState;
import com.tpinf4067.sale_vehicle.patterns.payment.PaymentType;

public class CreditOrderFactory implements OrderFactory {
    @Override
    public Order createOrder(Cart cart, PaymentType paymentType) {
        if (paymentType != PaymentType.CREDIT) {
            throw new IllegalArgumentException("Cette factory ne gère que les paiements à crédit.");
        }
        Order order = new Order();
        order.setState(new PendingState()); // ✅ Correct : État initial
        order.setPaymentType(paymentType);
        return order;
    }
}

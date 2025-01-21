package com.tpinf4067.sale_vehicle.patterns.order.observer;

import com.tpinf4067.sale_vehicle.repository.OrderObserver;

public class EmailOrderNotifier implements OrderObserver {
    private String email;

    public EmailOrderNotifier(String email) {
        this.email = email;
    }

    @Override
    public void update(String message) {
        System.out.println("📧 Email envoyé à " + email + " : " + message);
    }
}

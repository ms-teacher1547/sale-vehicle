package com.tpinf4067.sale_vehicle.patterns.catalog.observer;

public class EmailNotifier implements Observer {
    @Override
    public void update(String message) {
        System.out.println("📩 Notification par e-mail : " + message);
    }
}

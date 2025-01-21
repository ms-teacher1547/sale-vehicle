package com.tpinf4067.sale_vehicle.patterns.order.observer;

import java.util.ArrayList;
import java.util.List;

import com.tpinf4067.sale_vehicle.repository.OrderObserver;

public class OrderNotifier {
    private List<OrderObserver> observers = new ArrayList<>();

    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String message) {
        for (OrderObserver observer : observers) {
            observer.update(message);
        }
    }
}

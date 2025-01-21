package com.tpinf4067.sale_vehicle.patterns.catalog.observer;

public interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(String message);
}

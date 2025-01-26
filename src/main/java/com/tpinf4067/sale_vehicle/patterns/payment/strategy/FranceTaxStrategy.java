package com.tpinf4067.sale_vehicle.patterns.payment.strategy;

public class FranceTaxStrategy implements TaxStrategy {
    @Override
    public double calculateTax(double amount) {
        return amount * 0.20; // TVA 20%
    }
}
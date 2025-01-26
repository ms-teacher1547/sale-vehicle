package com.tpinf4067.sale_vehicle.patterns.payment.strategy;

public class ChadTaxStrategy implements TaxStrategy {
    @Override
    public double calculateTax(double amount) {
        return amount * 0.15; // TVA 15%
    }
}

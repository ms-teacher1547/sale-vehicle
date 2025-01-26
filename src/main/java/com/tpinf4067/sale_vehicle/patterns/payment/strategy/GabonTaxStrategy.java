package com.tpinf4067.sale_vehicle.patterns.payment.strategy;

public class GabonTaxStrategy implements TaxStrategy {
    @Override
    public double calculateTax(double amount) {
        return amount * 0.07; // TVA 7%
    }
}
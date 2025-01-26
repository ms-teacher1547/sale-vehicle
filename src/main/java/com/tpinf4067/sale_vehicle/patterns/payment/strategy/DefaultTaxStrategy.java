package com.tpinf4067.sale_vehicle.patterns.payment.strategy;

public class DefaultTaxStrategy implements TaxStrategy {
    @Override
    public double calculateTax(double amount) {
        return amount * 0.10; // Taxe par défaut de 10%
    }
}

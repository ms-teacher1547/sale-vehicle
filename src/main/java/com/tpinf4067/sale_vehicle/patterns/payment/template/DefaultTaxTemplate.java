package com.tpinf4067.sale_vehicle.patterns.payment.template;

public class DefaultTaxTemplate implements TaxTemplate {
    @Override
    public double calculateTax(double amount) {
        return amount * 0.10; // Taxe par défaut de 10%
    }
}

package com.tpinf4067.sale_vehicle.patterns.payment.template;

public class ChadTaxTemplate implements TaxTemplate {
    @Override
    public double calculateTax(double amount) {
        return amount * 0.15; // TVA 15%
    }
}

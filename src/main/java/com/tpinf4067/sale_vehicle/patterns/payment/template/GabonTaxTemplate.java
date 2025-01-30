package com.tpinf4067.sale_vehicle.patterns.payment.template;

public class GabonTaxTemplate implements TaxTemplate {
    @Override
    public double calculateTax(double amount) {
        return amount * 0.07; // TVA 7%
    }
}
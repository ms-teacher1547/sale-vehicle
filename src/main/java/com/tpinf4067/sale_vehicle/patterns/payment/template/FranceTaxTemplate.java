package com.tpinf4067.sale_vehicle.patterns.payment.template;

public class FranceTaxTemplate implements TaxTemplate {
    @Override
    public double calculateTax(double amount) {
        return amount * 0.20; // TVA 20%
    }
}
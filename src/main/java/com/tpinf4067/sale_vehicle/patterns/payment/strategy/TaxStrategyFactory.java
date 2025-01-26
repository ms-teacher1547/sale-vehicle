package com.tpinf4067.sale_vehicle.patterns.payment.strategy;

import java.util.HashMap;
import java.util.Map;

public class TaxStrategyFactory {
    private static final Map<String, TaxStrategy> strategies = new HashMap<>();

    static {
        strategies.put("FRANCE", new FranceTaxStrategy());
        strategies.put("SENEGAL", new SenegalTaxStrategy());
        strategies.put("GABON", new GabonTaxStrategy());
        strategies.put("TCHAD", new ChadTaxStrategy());
    }

    public static TaxStrategy getTaxStrategy(String country) {
        return strategies.getOrDefault(country.toUpperCase(), amount -> amount * 0.10); // Taxe par défaut 10%
    }
}

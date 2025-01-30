package com.tpinf4067.sale_vehicle.patterns.payment.template;

import java.util.HashMap;
import java.util.Map;

public class TaxTemplateFactory {
    private static final Map<String, TaxTemplate> strategies = new HashMap<>();

    static {
        strategies.put("FRANCE", new FranceTaxTemplate());
        strategies.put("SENEGAL", new SenegalTaxTemplate());
        strategies.put("GABON", new GabonTaxTemplate());
        strategies.put("TCHAD", new ChadTaxTemplate());
    }

    public static TaxTemplate getTaxStrategy(String country) {
        return strategies.getOrDefault(country.toUpperCase(), amount -> amount * 0.10); // Taxe par défaut 10%
    }
}

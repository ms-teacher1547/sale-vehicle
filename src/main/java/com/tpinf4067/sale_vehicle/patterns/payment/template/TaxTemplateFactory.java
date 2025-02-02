package com.tpinf4067.sale_vehicle.patterns.payment.template;

import java.util.HashMap;
import java.util.Map;

public class TaxTemplateFactory {
    private static final Map<String, TaxTemplate> templates = new HashMap<>();

    static {
        templates.put("FRANCE", new FranceTaxTemplate());
        templates.put("SENEGAL", new SenegalTaxTemplate());
        templates.put("GABON", new GabonTaxTemplate());
        templates.put("TCHAD", new ChadTaxTemplate());
    }

    public static TaxTemplate getTaxStrategy(String country) {
        return templates.getOrDefault(country.toUpperCase(), amount -> amount * 0.10); // Taxe par défaut 10%
    }
}

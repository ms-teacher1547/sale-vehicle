package com.tpinf4067.sale_vehicle.patterns.document;

public class DocumentDirector {
    private DocumentBuilder builder;

    public DocumentDirector(DocumentBuilder builder) {
        this.builder = builder;
    }

    public void constructOrderDocument(String vehicleName, double price) {
        builder.setTitle("Bon de Commande");
        builder.setContent("Véhicule commandé : " + vehicleName + " | Prix : " + price);
    }

    public Document getDocument() {
        return builder.getDocument();
    }
}

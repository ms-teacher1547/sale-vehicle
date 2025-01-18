package com.tpinf4067.sale_vehicle.service.document;

import com.tpinf4067.sale_vehicle.service.order.Order;

public class OrderDocumentBuilder implements DocumentBuilder {
    private Document document;

    public OrderDocumentBuilder() {
        this.document = new Document();
    }

    @Override
    public void setTitle(String title) {
        document.setTitle(title);
    }

    @Override
    public void setContent(String content) {
        document.setContent(content);
    }

    @Override
    public Document getDocument() {
        return this.document;
    }

    public void constructOrderDocument(Order order) {
        setTitle("Bon de Commande");
        setContent("Commande pour le véhicule : " + order.getVehicle().getName() + " | Prix : " + order.getVehicle().getPrice());
    }
}

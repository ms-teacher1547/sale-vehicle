package com.tpinf4067.sale_vehicle.service.document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        setTitle("📄 Bon de Commande");

        // 🔥 Récupération des informations
        String clientName = (order.getCustomer() != null) ? order.getCustomer().getName() : "Client inconnu";
        String clientEmail = (order.getCustomer() != null) ? order.getCustomer().getEmail() : "Non fourni";
        String clientAddress = (order.getCustomer() != null) ? order.getCustomer().getAddress() : "Non fournie";

        String vehicleName = order.getVehicle().getName();
        double vehiclePrice = order.getVehicle().getPrice();

        // 🔥 Récupération de la date et formatage
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedDate = now.format(formatter);

        // 🔥 Construction du contenu structuré en HTML
        String content = "<p><strong>🛒 Détails de la commande</strong></p>" +
                         "<hr>" +
                         "<p><strong>Véhicule :</strong> " + vehicleName + "</p>" +
                         "<p><strong>Prix :</strong> " + vehiclePrice + " €</p>" +
                         "<hr>" +
                         "<p><strong>👤 Informations du Client</strong></p>" +
                         "<p><strong>Nom :</strong> " + clientName + "</p>" +
                         "<p><strong>Email :</strong> " + clientEmail + "</p>" +
                         "<p><strong>Adresse :</strong> " + clientAddress + "</p>" +
                         "<hr>" +
                        //  "<p><strong>📅 Statut de la commande :</strong> " + order.getStatus() + "</p>" +
                        //  "<hr>" +
                         "<p><strong>🕒 Date de génération :</strong> " + formattedDate + "</p>";

        setContent(content);
    }
}

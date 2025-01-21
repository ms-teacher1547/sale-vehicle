package com.tpinf4067.sale_vehicle.patterns.document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.tpinf4067.sale_vehicle.domain.Option;
import com.tpinf4067.sale_vehicle.domain.Vehicle;
import com.tpinf4067.sale_vehicle.patterns.order.Order;

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

    public void constructOrderDocuments(Order order) {
        String clientName = order.getCustomer() != null ? order.getCustomer().getName() : "Client inconnu";
        String clientEmail = order.getCustomer() != null ? order.getCustomer().getEmail() : "Non fourni";
        String clientAddress = order.getCustomer() != null ? order.getCustomer().getAddress() : "Non fournie";

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedDate = now.format(formatter);

        // 📌 **Bon de commande**
        Document orderDoc = new Document();
        orderDoc.setTitle("📄 Bon de Commande");

        StringBuilder orderContent = new StringBuilder();
        orderContent.append("<p><strong>🛒 Détails de la commande</strong></p>");
        orderContent.append("<hr>");

        double totalPrice = 0;
        for (Vehicle vehicle : order.getVehicles()) {
            orderContent.append("<p><strong>Véhicule :</strong> ").append(vehicle.getName()).append("</p>");
            orderContent.append("<p><strong>Prix :</strong> ").append(vehicle.getPrice()).append(" €</p>");

            // Ajout des options
            List<Option> options = order.getOptions();
            if (!options.isEmpty()) {
                orderContent.append("<p><strong>Options :</strong> ");
                orderContent.append(options.stream().map(Option::getName).collect(Collectors.joining(", ")));
                orderContent.append("</p>");
            }

            orderContent.append("<hr>");
            totalPrice += vehicle.getPrice();
        }

        orderContent.append("<p><strong>💰 Total :</strong> ").append(totalPrice).append(" €</p>");
        orderContent.append("<hr>");
        orderContent.append("<p><strong>👤 Informations du Client</strong></p>");
        orderContent.append("<p><strong>Nom :</strong> ").append(clientName).append("</p>");
        orderContent.append("<p><strong>Email :</strong> ").append(clientEmail).append("</p>");
        orderContent.append("<p><strong>Adresse :</strong> ").append(clientAddress).append("</p>");
        orderContent.append("<hr>");
        orderContent.append("<p><strong>🕒 Date de génération :</strong> ").append(formattedDate).append("</p>");

        orderDoc.setContent(orderContent.toString());
        DocumentLiasseSingleton.getInstance().addDocument(orderDoc);

        // 📌 **Demande d'immatriculation**
        Document immatriculationDoc = new Document();
        immatriculationDoc.setTitle("📄 Demande d'Immatriculation");

        StringBuilder immatriculationContent = new StringBuilder();
        immatriculationContent.append("<p><strong>🚗 Demande d'immatriculation</strong></p>");
        immatriculationContent.append("<hr>");
        for (Vehicle vehicle : order.getVehicles()) {
            immatriculationContent.append("<p><strong>Véhicule :</strong> ").append(vehicle.getName()).append("</p>");
        }
        immatriculationContent.append("<p><strong>Client :</strong> ").append(clientName).append("</p>");
        immatriculationContent.append("<p><strong>Date :</strong> ").append(formattedDate).append("</p>");

        immatriculationDoc.setContent(immatriculationContent.toString());
        DocumentLiasseSingleton.getInstance().addDocument(immatriculationDoc);

        // 📌 **Certificat de Cession**
        Document cessionDoc = new Document();
        cessionDoc.setTitle("📄 Certificat de Cession");

        StringBuilder cessionContent = new StringBuilder();
        cessionContent.append("<p><strong>🚗 Certificat de vente</strong></p>");
        cessionContent.append("<hr>");
        for (Vehicle vehicle : order.getVehicles()) {
            cessionContent.append("<p><strong>Véhicule :</strong> ").append(vehicle.getName()).append("</p>");
        }
        cessionContent.append("<p><strong>Vendeur :</strong> Entreprise XYZ</p>");
        cessionContent.append("<p><strong>Acheteur :</strong> ").append(clientName).append("</p>");
        cessionContent.append("<p><strong>Prix :</strong> ").append(totalPrice).append(" €</p>");
        cessionContent.append("<p><strong>Date de génération :</strong> ").append(formattedDate).append("</p>");

        cessionDoc.setContent(cessionContent.toString());
        DocumentLiasseSingleton.getInstance().addDocument(cessionDoc);
    }
}

package com.tpinf4067.sale_vehicle.service.document;

import java.util.ArrayList;
import java.util.List;

public class DocumentLiasseSingleton {
    private static DocumentLiasseSingleton instance;
    private List<Document> documents;

    private DocumentLiasseSingleton() {
        documents = new ArrayList<>();
    }

    public static DocumentLiasseSingleton getInstance() {
        if (instance == null) {
            instance = new DocumentLiasseSingleton();
        }
        return instance;
    }

    public void addDocument(Document document) {
        documents.add(document);
    }

    public void showAllDocuments() {
        System.out.println("📂 Liasse de documents : ");
        for (Document doc : documents) {
            doc.showDocument();
        }
    }
}

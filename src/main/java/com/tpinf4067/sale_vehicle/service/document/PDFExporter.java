package com.tpinf4067.sale_vehicle.service.document;

import com.itextpdf.html2pdf.HtmlConverter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class PDFExporter {
    public void exportToPDF(com.tpinf4067.sale_vehicle.service.document.Document customDocument, String filePath) {
        try {
            // Vérifier si le dossier "documents" existe, sinon le créer
            File directory = new File("documents");
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    System.out.println("📂 Dossier 'documents/' créé avec succès.");
                } else {
                    System.out.println("❌ Impossible de créer le dossier 'documents/' !");
                }
            }

            // 🔥 Mise en forme améliorée avec HTML et CSS
            String htmlContent = "<html><head><style>" +
                    "body { font-family: Arial, sans-serif; margin: 20px; }" +
                    "h1 { text-align: center; font-size: 22px; color: #333; }" +
                    "p { font-size: 14px; margin-bottom: 10px; }" +
                    "hr { border: 1px solid #ddd; margin: 10px 0; }" +
                    "strong { color: #555; }" +
                    "</style></head><body>" +
                    "<h1>" + customDocument.getTitle() + "</h1>" +
                    "<hr>" +
                    customDocument.getContent() +
                    "</body></html>";

            // Conversion du HTML en PDF
            OutputStream outputStream = new FileOutputStream(filePath);
            HtmlConverter.convertToPdf(htmlContent, outputStream);

            System.out.println("✅ Fichier PDF généré avec succès : " + filePath);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'exportation en PDF : " + e.getMessage());
            e.printStackTrace();
        }
    }
}

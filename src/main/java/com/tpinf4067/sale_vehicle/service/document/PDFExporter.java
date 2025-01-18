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

            // Vérifier si le fichier peut être créé
            File pdfFile = new File(filePath);
            System.out.println("📄 Création du fichier PDF : " + pdfFile.getAbsolutePath());

            // Création du contenu HTML pour le PDF
            String htmlContent = "<html><head><style>" +
                    "body { font-family: Arial, sans-serif; }" +
                    "h1 { text-align: center; font-size: 18px; }" +
                    "p { font-size: 14px; text-align: justify; }" +
                    "</style></head><body>" +
                    "<h1>" + customDocument.getTitle() + "</h1>" +
                    "<p>" + customDocument.getContent() + "</p>" +
                    "</body></html>";

            // Conversion du HTML en PDF
            OutputStream outputStream = new FileOutputStream(pdfFile);
            HtmlConverter.convertToPdf(htmlContent, outputStream);

            System.out.println("✅ Fichier PDF généré avec succès : " + pdfFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'exportation en PDF : " + e.getMessage());
            e.printStackTrace();
        }
    }
}

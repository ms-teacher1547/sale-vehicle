package com.tpinf4067.sale_vehicle.service.document;

public class PDFDocumentAdapter {
    private final PDFExporter pdfExporter;

    public PDFDocumentAdapter() {
        this.pdfExporter = new PDFExporter();
    }

    public void export(Document document) {
        String filePath = "documents/" + document.getTitle().replace(" ", "_") + ".pdf";
        System.out.println("📄 Tentative d'exportation du document en PDF : " + filePath);
        pdfExporter.exportToPDF(document, filePath);
    }
}

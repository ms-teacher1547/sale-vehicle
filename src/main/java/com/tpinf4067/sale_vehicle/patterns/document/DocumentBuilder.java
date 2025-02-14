package com.tpinf4067.sale_vehicle.patterns.document;

public interface DocumentBuilder {
    void setTitle(String title);
    void setContent(String content);
    Document getDocument();
    void export(Document document);

}

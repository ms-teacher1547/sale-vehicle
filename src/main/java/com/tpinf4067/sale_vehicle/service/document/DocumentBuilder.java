package com.tpinf4067.sale_vehicle.service.document;

public interface DocumentBuilder {
    void setTitle(String title);
    void setContent(String content);
    Document getDocument();
}

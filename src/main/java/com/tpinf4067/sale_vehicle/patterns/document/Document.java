package com.tpinf4067.sale_vehicle.patterns.document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Document {
    private String title;
    private String content;
    public String filename;

    public void showDocument() {
        System.out.println(  title + " : " + content);
    }
}

package com.tpinf4067.sale_vehicle.patterns.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentInvoiceDTO {
    public PaymentInvoiceDTO(Long id2, String title2, String filename2, String content2, Long long1, Long long2) {
        //TODO Auto-generated constructor stub
    }
    private Long id;
    private String title;
    private String filename;
    private String content;
    private Long orderId;
    private Long paymentId;

    // Constructeur et Getters/Setters
}


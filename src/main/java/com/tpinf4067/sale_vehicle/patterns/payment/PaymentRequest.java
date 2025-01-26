package com.tpinf4067.sale_vehicle.patterns.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private Long orderId;
    private PaymentType paymentType;
    private String country;
}

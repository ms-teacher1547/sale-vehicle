package com.tpinf4067.sale_vehicle.patterns.order.factory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
    private Long customerId;
    private String paymentType;
}

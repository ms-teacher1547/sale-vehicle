package com.tpinf4067.sale_vehicle.service.customer;

import com.tpinf4067.sale_vehicle.service.customer.enums.CustomerType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String address;

    @Enumerated(EnumType.STRING)
    private CustomerType type;
}

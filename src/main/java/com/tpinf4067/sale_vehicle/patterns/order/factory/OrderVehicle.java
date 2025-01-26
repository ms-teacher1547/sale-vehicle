package com.tpinf4067.sale_vehicle.patterns.order.factory;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.tpinf4067.sale_vehicle.domain.Vehicle;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_vehicles")
public class OrderVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    private Order order;

    @ManyToOne
    private Vehicle vehicle;

    @Column(nullable = false)
    private int quantity;
}

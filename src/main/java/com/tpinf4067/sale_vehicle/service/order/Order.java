package com.tpinf4067.sale_vehicle.service.order;

import com.tpinf4067.sale_vehicle.domain.Vehicle;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Vehicle vehicle;

    private String status; // "En cours", "Validée", "Livrée"
}

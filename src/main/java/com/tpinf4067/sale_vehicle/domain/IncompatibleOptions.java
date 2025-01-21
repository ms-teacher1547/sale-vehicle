package com.tpinf4067.sale_vehicle.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "incompatible_options")
public class IncompatibleOptions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Option option1; // Première option

    @ManyToOne
    private Option option2; // Deuxième option
}

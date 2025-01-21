package com.tpinf4067.sale_vehicle.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "options")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Nom de l'option (ex: Sièges en cuir)
    private double price; // Prix de l'option

    @Enumerated(EnumType.STRING)
    private OptionCategory category; // Catégorie (ex: CONFORT, PERFORMANCE)
}

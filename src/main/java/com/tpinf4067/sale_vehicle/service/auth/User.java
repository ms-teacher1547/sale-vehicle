package com.tpinf4067.sale_vehicle.service.auth;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "username") })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)  // Ajout de l'unicité au niveau JPA
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}

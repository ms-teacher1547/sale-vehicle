package com.tpinf4067.sale_vehicle.patterns.auth;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tpinf4067.sale_vehicle.patterns.customer.Customer;

@Entity
@Table(name = "users")
@Setter
@Getter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String fullname; // 🔥 Stocke le nom complet de l'utilisateur
    private String email; // 🔥 Ajout de l'email

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", unique = true)
    @JsonManagedReference // ✅ Permet la sérialisation correcte de la relation
    private Customer customer; // 🔥 Relation avec Customer

    public User() {}

    // ✅ Constructeur pour un USER (associé à un Customer)
    public User(String username, String password, Role role, String fullname, String email) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullname = fullname;
        this.email = email;
    }

    // ✅ Constructeur pour un ADMIN (sans Customer)
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullname = username;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }

    public String getFullname() { return fullname; }
    public String getEmail() { return email; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}

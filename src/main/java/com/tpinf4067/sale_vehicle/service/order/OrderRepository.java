package com.tpinf4067.sale_vehicle.service.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // ✅ Trouver les commandes par ID de client
    List<Order> findByCustomerId(Long customerId);
    // ✅ Trouver les commandes par ID de véhicule
    List<Order> findByStateIgnoreCase(String state); // ✅ Ignorer la casse
}


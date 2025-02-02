package com.tpinf4067.sale_vehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.tpinf4067.sale_vehicle.patterns.customer.Customer;
import com.tpinf4067.sale_vehicle.patterns.customer.enums.CustomerType;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByNameContainingIgnoreCase(String name);
    List<Customer> findByType(CustomerType type);
    
    // ✅ Trouver un client par email
    Optional<Customer> findByEmail(String email);

    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.subsidiaries WHERE c.id = :id")
    Optional<Customer> findByIdWithSubsidiaries(@Param("id") Long id);

}

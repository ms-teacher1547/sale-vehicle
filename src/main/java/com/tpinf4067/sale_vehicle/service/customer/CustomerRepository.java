package com.tpinf4067.sale_vehicle.service.customer;

import com.tpinf4067.sale_vehicle.service.customer.enums.CustomerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByNameContainingIgnoreCase(String name);
    List<Customer> findByType(CustomerType type);
}

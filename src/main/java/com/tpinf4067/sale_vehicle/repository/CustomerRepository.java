package com.tpinf4067.sale_vehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpinf4067.sale_vehicle.patterns.customer.Customer;
import com.tpinf4067.sale_vehicle.patterns.customer.enums.CustomerType;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByNameContainingIgnoreCase(String name);
    List<Customer> findByType(CustomerType type);
}

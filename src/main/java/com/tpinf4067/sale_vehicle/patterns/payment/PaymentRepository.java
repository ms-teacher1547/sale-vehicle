package com.tpinf4067.sale_vehicle.patterns.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpinf4067.sale_vehicle.patterns.order.Order;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByOrderAndStatus(Order order, PaymentStatus paye);
}

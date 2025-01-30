package com.tpinf4067.sale_vehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tpinf4067.sale_vehicle.patterns.order.factory.Order;
import com.tpinf4067.sale_vehicle.patterns.payment.Payment;
import com.tpinf4067.sale_vehicle.patterns.payment.PaymentStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // ✅ Récupérer les paiements d'un client via son order.customer.id
    List<Payment> findByOrder_CustomerId(Long customerId);

    // ✅ Trouver le dernier paiement en attente d’un client
    Optional<Payment> findFirstByOrderCustomerIdAndStatus(Long customerId, PaymentStatus status);

    // ✅ Vérifier si un paiement a déjà été effectué pour une commande
    boolean existsByOrderAndStatus(Order order, PaymentStatus status);
}

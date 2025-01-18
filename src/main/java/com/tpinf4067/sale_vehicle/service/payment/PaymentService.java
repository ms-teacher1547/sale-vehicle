package com.tpinf4067.sale_vehicle.service.payment;

import com.tpinf4067.sale_vehicle.service.order.Order;
import com.tpinf4067.sale_vehicle.service.order.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public Payment processPayment(Long orderId, PaymentType paymentType, String country) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isEmpty()) {
            return null;
        }

        Order order = orderOptional.get();

        // 🔥 Calcul des taxes (exemple : 20% si pays = FR, sinon 15%)
        double taxRate = country.equalsIgnoreCase("FR") ? 0.20 : 0.15;
        double tax = order.getVehicle().getPrice() * taxRate;
        double totalAmount = order.getVehicle().getPrice() + tax;

        // 🔥 Création du paiement
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentType(paymentType);
        payment.setPaymentStatus(PaymentStatus.EN_ATTENTE); // Par défaut, en attente
        payment.setAmount(totalAmount);
        payment.setTax(tax);
        payment.setCountry(country);

        return paymentRepository.save(payment);
    }

    public Payment confirmPayment(Long paymentId) {
        return paymentRepository.findById(paymentId).map(payment -> {
            payment.setPaymentStatus(PaymentStatus.PAYE);
            return paymentRepository.save(payment);
        }).orElse(null);
    }

    public Payment rejectPayment(Long paymentId) {
        return paymentRepository.findById(paymentId).map(payment -> {
            payment.setPaymentStatus(PaymentStatus.REFUSE);
            return paymentRepository.save(payment);
        }).orElse(null);
    }
}

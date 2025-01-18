package com.tpinf4067.sale_vehicle.web;

import com.tpinf4067.sale_vehicle.service.payment.Payment;
import com.tpinf4067.sale_vehicle.service.payment.PaymentService;
import com.tpinf4067.sale_vehicle.service.payment.PaymentType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/")
    public ResponseEntity<Payment> processPayment(@RequestParam Long orderId, @RequestParam PaymentType paymentType, @RequestParam String country) {
        Payment payment = paymentService.processPayment(orderId, paymentType, country);
        return payment != null ? ResponseEntity.ok(payment) : ResponseEntity.badRequest().build();
    }

    @PutMapping("/{paymentId}/confirm")
    public ResponseEntity<Payment> confirmPayment(@PathVariable Long paymentId) {
        Payment payment = paymentService.confirmPayment(paymentId);
        return payment != null ? ResponseEntity.ok(payment) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{paymentId}/reject")
    public ResponseEntity<Payment> rejectPayment(@PathVariable Long paymentId) {
        Payment payment = paymentService.rejectPayment(paymentId);
        return payment != null ? ResponseEntity.ok(payment) : ResponseEntity.notFound().build();
    }
}

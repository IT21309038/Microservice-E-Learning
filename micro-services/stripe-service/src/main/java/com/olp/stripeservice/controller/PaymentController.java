package com.olp.stripeservice.controller;

import com.olp.stripeservice.dto.PaymentChargeDto;
import com.olp.stripeservice.model.Payment;
import com.olp.stripeservice.repository.PaymentRepository;
import com.olp.stripeservice.service.PaymentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/stripe")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @PostMapping("/charge")
    @ResponseBody
    @CircuitBreaker(name = "stripeService", fallbackMethod = "chargeFallback")
    public PaymentChargeDto charge(@RequestBody PaymentChargeDto model) {

        return paymentService.charge(model);
    }

    public PaymentChargeDto chargeFallback(Throwable throwable) {
        return new PaymentChargeDto();
    }

    @GetMapping("/payments")
    @ResponseBody
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @GetMapping("/payments/{id}")
    @ResponseBody
    public ResponseEntity<Payment> getPaymentById(@PathVariable(value = "id") String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoSuchElementException("Payment not found with id: " + paymentId));
        return ResponseEntity.ok().body(payment);
    }
}

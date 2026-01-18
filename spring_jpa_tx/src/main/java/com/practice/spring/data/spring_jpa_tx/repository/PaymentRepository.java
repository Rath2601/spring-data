package com.practice.spring.data.spring_jpa_tx.repository;

import com.practice.spring.data.spring_jpa_tx.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}

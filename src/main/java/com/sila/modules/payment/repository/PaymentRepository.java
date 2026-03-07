package com.sila.modules.payment.repository;

import com.sila.modules.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PaymentRepository
    extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
  boolean existsByUserIdAndCourseId(Long userId, Long courseId);
}

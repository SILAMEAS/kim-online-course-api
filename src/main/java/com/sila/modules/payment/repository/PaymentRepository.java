package com.sila.modules.payment.repository;

import com.sila.modules.payment.model.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository
    extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
  boolean existsByUserIdAndCourseId(Long userId, Long courseId);

  @Query(
"""
SELECT p
FROM Payment p
JOIN FETCH p.user
WHERE p.id = :id
""")
  Optional<Payment> findWithUser(Long id);
}

package com.sila.modules.payment.repository;

import com.sila.modules.payment.model.Payment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PaymentRepositoryCustomImpl implements PaymentRepositoryCustom {

  @PersistenceContext private EntityManager entityManager;

  /**
   * Calculate the total amount of payments matching the given specification.
   *
   * @param spec Dynamic filtering spec (can be searched, byOwnership, byUserId, etc.)
   * @return Total sum of payment amounts (0 if none)
   */
  public Double sumAmount(Specification<Payment> spec) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Double> query = cb.createQuery(Double.class);
    Root<Payment> root = query.from(Payment.class);

    // Select SUM(amount) with COALESCE to return 0 if no matching rows
    query.select(cb.coalesce(cb.sum(root.get("amount")), 0d));

    // Apply dynamic specification if provided
    if (spec != null) {
      query.where(spec.toPredicate(root, query, cb));
    }

    return entityManager.createQuery(query).getSingleResult();
  }
}

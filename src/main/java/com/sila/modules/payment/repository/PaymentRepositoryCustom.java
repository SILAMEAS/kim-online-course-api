package com.sila.modules.payment.repository;

import com.sila.modules.payment.model.Payment;
import org.springframework.data.jpa.domain.Specification;

public interface PaymentRepositoryCustom {

  public Double sumAmount(Specification<Payment> spec);
}

package com.sila.modules.payment.spec;

import com.sila.modules.payment.model.Payment;
import com.sila.modules.payment.model.Payment_;
import com.sila.modules.profile.model.User_;
import com.sila.modules.video.model.Video_;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentSpec {

  public static Specification<Payment> search(String search) {
    return (var root, var query, var cb) -> {
      if (search == null || search.isBlank()) {
        return cb.conjunction();
      }

      final var like = "%" + search.toLowerCase(Locale.ENGLISH).trim() + "%";

      return cb.or(
          cb.like(cb.lower(root.get(Video_.COURSE)), like),
          cb.like(cb.lower(root.get(Video_.TITLE)), like));
    };
  }

  public static Specification<Payment> byUserId(Long userId) {
    return (root, query, cb) -> {
      if (userId == null) {
        return cb.conjunction();
      }

      return cb.equal(root.get(Payment_.USER).get(User_.ID), userId);
    };
  }
}

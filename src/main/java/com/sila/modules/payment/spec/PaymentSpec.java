package com.sila.modules.payment.spec;

import com.sila.config.context.UserContext;
import com.sila.modules.payment.model.Payment;
import com.sila.modules.payment.model.Payment_;
import com.sila.modules.profile.model.User_;
import com.sila.modules.video.model.Video_;
import com.sila.share.enums.ROLE;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for querying {@link Payment} entities.
 *
 * <p>Provides dynamic search and filtering capabilities based on payment, user, and related course
 * data.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentSpec {

  /**
   * Builds a {@link Specification} to search payments by course title or video title.
   *
   * <p>If the search string is null or blank, returns a specification that matches all payments.
   *
   * @param search The search string to match against course title or video title
   * @return A {@link Specification} for filtering payments
   */
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

  /**
   * Builds a {@link Specification} to filter payments based on the current user's ownership.
   *
   * <p>ADMIN users can access all payments; other users can only access their own payments.
   *
   * @return A {@link Specification} for ownership filtering
   */
  public static Specification<Payment> byOwnership() {
    return (root, query, cb) -> {
      if (UserContext.getUserRole() == ROLE.ADMIN) {
        return cb.conjunction();
      }
      return cb.equal(root.get(Payment_.USER).get(User_.ID), UserContext.getUserId());
    };
  }

  /**
   * Builds a {@link Specification} to filter payments by a specific user ID.
   *
   * <p>If the userId is null, returns a specification that matches all payments.
   *
   * @param userId ID of the user
   * @return A {@link Specification} for filtering by user
   */
  public static Specification<Payment> byUserId(Long userId) {
    return (root, query, cb) -> {
      if (userId == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get(Payment_.USER).get(User_.ID), userId);
    };
  }
}

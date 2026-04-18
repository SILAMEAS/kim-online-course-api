package com.sila.modules.wishlist.spec;

import com.sila.modules.enrolment.model.Enrollment;
import com.sila.modules.wishlist.model.Wishlist;
import jakarta.persistence.criteria.JoinType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for querying {@link Enrollment} entities.
 *
 * <p>Provides dynamic search and filtering capabilities based on enrollment, course, and user data.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WishlistSpec {

  public static Specification<Wishlist> byUserId(Long userId) {
    return (root, query, cb) -> {

      // IMPORTANT: only apply fetch for non-count queries
      if (Wishlist.class.equals(query.getResultType())) {
        root.fetch("course", JoinType.LEFT);
        root.fetch("user", JoinType.LEFT);
        query.distinct(true);
      }

      return cb.equal(root.get("user").get("id"), userId);
    };
  }
}

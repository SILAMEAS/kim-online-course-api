package com.sila.modules.wishlist.spec;

import com.sila.config.context.UserContext;
import com.sila.modules.course.Enum.CourseStatus;
import com.sila.modules.course.model.Course_;
import com.sila.modules.enrolment.model.Enrollment;
import com.sila.modules.wishlist.model.Wishlist;
import com.sila.modules.wishlist.model.Wishlist_;
import com.sila.share.enums.ROLE;
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

  public static Specification<Wishlist> fetchRelationsDetailOptimized() {
    return (var root, var query, var cb) -> {
      if (Wishlist.class.equals(query.getResultType())) {
        root.fetch(Wishlist_.COURSE, JoinType.LEFT);
      }
      return cb.conjunction();
    };
  }

  public static Specification<Wishlist> visibleByRole() {
    return (root, query, cb) -> {

      // ADMIN → see all wishlist items
      if (UserContext.getUserRole() == ROLE.ADMIN) {
        return cb.conjunction();
      }

      // STUDENT / TEACHER → only wishlist items where course is PUBLISHED
      return cb.equal(root.get(Wishlist_.COURSE).get(Course_.STATUS), CourseStatus.PUBLISHED);
    };
  }
}

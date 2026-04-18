package com.sila.modules.review.spec;

import com.sila.modules.review.model.Review;
import com.sila.modules.video.model.Video_;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for querying {@link } entities.
 *
 * <p>Provides dynamic search and filtering capabilities based on enrollment, course, and user data.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewSpec {

  public static Specification<Review> search(String search) {
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
  public static Specification<Review> hasCourseId(Long courseId) {
    return (root, query, cb) -> {
      query.distinct(false); // 🔥 prevent losing rows
      return cb.equal(root.get("course").get("id"), courseId);
    };
  }

}

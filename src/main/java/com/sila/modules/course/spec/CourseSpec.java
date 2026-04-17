package com.sila.modules.course.spec;

import com.sila.modules.course.Enum.LevelStatus;
import com.sila.modules.course.model.Category_;
import com.sila.modules.course.model.Course;
import com.sila.modules.course.model.Course_;
import com.sila.modules.enrolment.model.Enrollment;
import com.sila.modules.enrolment.model.Enrollment_;
import com.sila.modules.profile.model.User;
import com.sila.modules.profile.model.User_;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for querying {@link Course} entities.
 *
 * <p>Provides methods for dynamic search and filtering.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CourseSpec {

  /**
   * Builds a {@link Specification} to search courses by title or description.
   *
   * <p>If the search string is null or blank, returns a specification that matches all courses.
   *
   * @param search The search string to match against course title or description
   * @return A {@link Specification} for filtering courses
   */
  public static Specification<Course> search(String search) {
    if (search == null || search.isBlank()) {
      return (var root, var query, var cb) -> cb.conjunction();
    }

    final var like = "%" + search.toLowerCase(Locale.ENGLISH).trim() + "%";

    return (var root, var query, var cb) ->
        cb.or(
            cb.like(cb.lower(root.get(Course_.TITLE)), like),
            cb.like(cb.lower(root.get(Course_.DESCRIPTION)), like));
  }

  public static Specification<Course> hasStudentEnrolled(Long studentId) {
    return (root, query, cb) -> {
      // 1. Create a subquery that looks at the Enrollment entity
      Subquery<Long> subquery = query.subquery(Long.class);
      Root<Enrollment> enrollmentRoot = subquery.from(Enrollment.class);

      // 2. Select the course ID from enrollments
      subquery.select(enrollmentRoot.get(Enrollment_.COURSE).get(Course_.ID));

      // 3. Filter the enrollments by the studentId AND link it to the Course root
      subquery.where(
          cb.equal(enrollmentRoot.get(Enrollment_.USER).get(User_.ID), studentId),
          cb.equal(enrollmentRoot.get(Enrollment_.COURSE).get(Course_.ID), root.get("id")));

      // 4. The Course is included if its ID exists in that subquery result
      return cb.exists(subquery);
    };
  }

  /** Filter by Course Level (e.g., BEGINNER, INTERMEDIATE) */
  public static Specification<Course> hasLevel(LevelStatus level) {
    return (root, query, cb) -> level == null ? null : cb.equal(root.get(Course_.LEVEL), level);
  }

  /** Filter by Category ID */
  public static Specification<Course> hasCategory(Long categoryId) {
    return (root, query, cb) ->
        categoryId == null
            ? null
            : cb.equal(root.get(Course_.CATEGORY).get(Category_.ID), categoryId);
  }

  /** Filter by Minimum Rating (e.g., courses with 4.0 stars and up) */
  public static Specification<Course> hasMinRating(Double minRating) {
    return (root, query, cb) ->
        minRating == null ? null : cb.greaterThanOrEqualTo(root.get(Course_.RATING), minRating);
  }

  /** Filter by Price Range */
  public static Specification<Course> priceBetween(Double minPrice, Double maxPrice) {
    return (root, query, cb) -> {
      if (minPrice != null && maxPrice != null) {
        return cb.between(root.get(Course_.PRICE), minPrice, maxPrice);
      } else if (minPrice != null) {
        return cb.greaterThanOrEqualTo(root.get(Course_.PRICE), minPrice);
      } else if (maxPrice != null) {
        return cb.lessThanOrEqualTo(root.get(Course_.PRICE), maxPrice);
      }
      return null;
    };
  }

  /** Filter by Course Level (e.g., BEGINNER, INTERMEDIATE) */
  public static Specification<Course> byTeacherId(Long teacherId) {
    return (root, query, cb) ->
        teacherId == null
            ? null
            : cb.equal(root.get(Course_.INSTRUCTOR).get(User_.ID), teacherId);
  }
}

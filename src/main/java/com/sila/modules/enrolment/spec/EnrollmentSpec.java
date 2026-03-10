package com.sila.modules.enrolment.spec;

import com.sila.config.context.UserContext;
import com.sila.modules.course.model.Course_;
import com.sila.modules.enrolment.model.Enrollment;
import com.sila.modules.enrolment.model.Enrollment_;
import com.sila.modules.profile.model.User_;
import com.sila.share.enums.ROLE;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for querying {@link Enrollment} entities.
 *
 * <p>Provides dynamic search and filtering capabilities based on enrollment, course, and user data.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnrollmentSpec {

  /**
   * Builds a {@link Specification} to search enrollments by course title or instructor's name.
   *
   * <p>If the search string is null or blank, returns a specification that matches all enrollments.
   *
   * @param search The search string to match against course title or instructor's name
   * @return A {@link Specification} for filtering enrollments
   */
  public static Specification<Enrollment> search(String search) {
    if (search == null || search.isBlank()) {
      return (var root, var query, var cb) -> cb.conjunction();
    }

    final var like = "%" + search.toLowerCase(Locale.ENGLISH).trim() + "%";

    return (var root, var query, var cb) ->
        cb.or(
            cb.like(cb.lower(root.get(Enrollment_.COURSE).get(Course_.TITLE)), like),
            cb.like(
                cb.lower(
                    root.get(Enrollment_.COURSE).get(Course_.INSTRUCTOR).get(User_.FIRST_NAME)),
                like),
            cb.like(
                cb.lower(root.get(Enrollment_.COURSE).get(Course_.INSTRUCTOR).get(User_.LAST_NAME)),
                like));
  }

  /**
   * Builds a {@link Specification} to filter enrollments by the current user's ownership.
   *
   * <p>ADMIN users can access all enrollments; other users can only access their own enrollments.
   *
   * @return A {@link Specification} for ownership filtering
   */
  public static Specification<Enrollment> byOwnership() {
    return (root, query, cb) -> {
      if (UserContext.getUserRole() == ROLE.ADMIN) {
        return cb.conjunction();
      }
      return cb.equal(root.get(Enrollment_.USER).get(User_.ID), UserContext.getUserId());
    };
  }

  /**
   * Builds a {@link Specification} to filter enrollments by a specific course ID.
   *
   * <p>If the courseId is null, returns a specification that matches all enrollments.
   *
   * @param courseId ID of the course
   * @return A {@link Specification} for filtering by course
   */
  public static Specification<Enrollment> byCourse(Long courseId) {
    return (root, query, cb) -> {
      if (courseId == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get(Enrollment_.COURSE).get(Course_.ID), courseId);
    };
  }
}

package com.sila.modules.enrolment.spec;

import com.sila.modules.course.model.Course_;
import com.sila.modules.enrolment.model.Enrollment;
import com.sila.modules.enrolment.model.Enrollment_;
import com.sila.modules.profile.model.User_;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnrollmentSpec {
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

  public static Specification<Enrollment> byUserId(Long userId) {
    return (root, query, cb) -> {
      if (userId == null) {
        return cb.conjunction();
      }

      return cb.equal(root.get(Enrollment_.USER).get(User_.ID), userId);
    };
  }
}

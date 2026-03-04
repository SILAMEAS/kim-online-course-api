package com.sila.modules.course.spec;

import com.sila.modules.course.model.Course;
// import com.sila.modules.course.model.Course_;
import com.sila.modules.course.model.Course_;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CourseSpec {
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
}

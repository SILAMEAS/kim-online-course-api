package com.sila.modules.course.spec;


import com.sila.modules.course.model.Course;
import com.sila.modules.course.model.Course_;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

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
                        cb.like(cb.lower(root.get(Course_.DESCRIPTION)), like)
                );
    }
}

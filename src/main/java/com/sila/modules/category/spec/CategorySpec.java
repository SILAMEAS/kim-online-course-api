package com.sila.modules.category.spec;

import com.sila.modules.category.model.Category;
import com.sila.modules.category.model.Category_;
import com.sila.modules.course.model.Course;
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
public class CategorySpec {

  /**
   * Builds a {@link Specification} to search courses by title or description.
   *
   * <p>If the search string is null or blank, returns a specification that matches all courses.
   *
   * @param search The search string to match against course title or description
   * @return A {@link Specification} for filtering courses
   */
  public static Specification<Category> search(String search) {
    if (search == null || search.isBlank()) {
      return (var root, var query, var cb) -> cb.conjunction();
    }

    final var like = "%" + search.toLowerCase(Locale.ENGLISH).trim() + "%";

    return (var root, var query, var cb) ->
        cb.or(cb.like(cb.lower(root.get(Category_.NAME)), like));
  }
}

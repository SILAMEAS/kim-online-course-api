package com.sila.modules.image.spec;

import com.sila.modules.image.model.Image;
import com.sila.modules.video.model.Video_;
import java.util.List;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for querying {@link Image} entities.
 *
 * <p>Provides dynamic search and filtering capabilities for videos, including by title and course.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageSpec {

  /**
   * Builds a {@link Specification} to search videos by title or course.
   *
   * <p>If the search string is null or blank, returns a specification that matches all videos.
   *
   * @param search The search string to match against video title or course
   * @return A {@link Specification} for filtering videos
   */
  public static Specification<Image> search(String search) {
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
   * Builds a {@link Specification} to filter videos by course ID.
   *
   * <p>If the courseId is null, returns a specification that matches all videos.
   *
   * @param courseId The ID of the course
   * @return A {@link Specification} for filtering videos by course
   */
  public static Specification<Image> byCourseId(Long courseId) {
    if (courseId == null) {
      return (var root, var query, var cb) -> cb.conjunction();
    }
    return (var root, var query, var cb) ->
        root.get(Video_.COURSE).get(Video_.ID).in(List.of(courseId));
  }
}

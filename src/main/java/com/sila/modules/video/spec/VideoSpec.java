package com.sila.modules.video.spec;

import com.sila.modules.video.model.Video;
import com.sila.modules.video.model.Video_;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VideoSpec {
  public static Specification<Video> search(String search) {
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
}

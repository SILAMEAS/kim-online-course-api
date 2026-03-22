package com.sila.modules.profile.spec;

import com.sila.modules.profile.model.User;
import com.sila.modules.profile.model.User_;
import com.sila.share.enums.ROLE;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for querying {@link User} entities.
 *
 * <p>Provides dynamic search capabilities based on user's first name, last name, or email.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSpec {

  /**
   * Builds a {@link Specification} to search users by first name, last name, or email.
   *
   * <p>If the search string is null or blank, returns a specification that matches all users.
   *
   * @param search The search string to match against first name, last name, or email
   * @return A {@link Specification} for filtering users
   */
  public static Specification<User> search(String search) {
    if (search == null || search.isBlank()) {
      return (var root, var query, var cb) -> cb.conjunction();
    }

    final var like = "%" + search.toLowerCase(Locale.ENGLISH).trim() + "%";

    return (var root, var query, var cb) ->
        cb.or(
            cb.like(cb.lower(root.get(User_.FIRST_NAME)), like),
            cb.like(cb.lower(root.get(User_.LAST_NAME)), like),
            cb.like(cb.lower(root.get(User_.EMAIL)), like));
  }

  public static Specification<User> byTeacher() {
    return (var root, var query, var cb) -> cb.equal(root.get(User_.ROLE), ROLE.INSTRUCTOR);
  }
}

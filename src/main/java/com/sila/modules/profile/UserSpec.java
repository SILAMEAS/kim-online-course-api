package com.sila.modules.profile;

import com.sila.modules.profile.model.User;
import com.sila.modules.profile.model.User_;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSpec {

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

  public static Specification<User> hasOrderedFromRestaurant(Long restaurantId) {
    return (var root, var query, var cb) -> {
      query.distinct(true);

      Join<?, ?> orders = root.join("orders", JoinType.INNER);
      return cb.equal(orders.get("restaurant").get("id"), restaurantId);
    };
  }
}

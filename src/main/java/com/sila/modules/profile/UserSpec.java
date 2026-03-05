package com.sila.modules.profile;

import com.sila.modules.profile.model.User;
import com.sila.modules.profile.model.User_;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

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

}

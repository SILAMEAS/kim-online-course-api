package com.sila.config.jpaConfig;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<Long> {

  @Override
  public Optional<Long> getCurrentAuditor() {
    // Example: you store user ID as a string in SecurityContext
    String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();

    try {
      Long userId = Long.parseLong(userIdStr); // convert String -> Long
      return Optional.of(userId);
    } catch (NumberFormatException e) {
      return Optional.empty(); // fallback if conversion fails
    }
  }
}

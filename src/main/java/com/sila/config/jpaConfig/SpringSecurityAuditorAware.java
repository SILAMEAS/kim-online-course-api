package com.sila.config.jpaConfig;

import com.sila.config.context.UserContext;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<Long> {

  @Override
  public Optional<Long> getCurrentAuditor() {

    try {
      return Optional.of(UserContext.getUserId());
    } catch (NumberFormatException e) {
      return Optional.empty(); // fallback if conversion fails
    }
  }
}

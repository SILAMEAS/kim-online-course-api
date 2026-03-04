package com.sila.config;

import com.sila.modules.profile.model.User;
import com.sila.modules.profile.repository.UserRepository;
import com.sila.share.enums.ROLE;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class StartupDataInitializer implements ApplicationRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {

    // Check if any user exists
    if (userRepository.count() == 0) {
      createUser("ADMIN", "admin@gmail.com", ROLE.ADMIN, "✅ Default ADMIN user created!");
      createUser("STUDENT", "student@gmail.com", ROLE.STUDENT, "✅ Default STUDENT user created!");
      createUser(
          "TEACHER", "teacher@gmail.com", ROLE.INSTRUCTOR, "✅ Default INSTRUCTOR user created!");
    }
  }

  private void createUser(String firstName, String email, ROLE role, String message) {
    // Create Admin User
    User admin = new User();
    admin.setEmail(email);
    admin.setPassword(passwordEncoder.encode("123"));
    admin.setFirstName(firstName);
    admin.setLastName("LA");
    admin.setRole(role);

    userRepository.save(admin);

    System.out.println(message);
  }
}

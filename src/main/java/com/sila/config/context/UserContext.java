package com.sila.config.context;

import com.sila.config.exception.BadRequestException;
import com.sila.modules.profile.model.User;
import com.sila.share.enums.ROLE;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserContext {

  private static final ThreadLocal<User> currentUser = new ThreadLocal<>();

  public static User getUser() {
    //    check user login or not
    checkLogin();
    return currentUser.get();
  }

  public static void setUser(User user) {
    currentUser.set(user);
  }

  public static Optional<User> findUser() {
    return Optional.ofNullable(currentUser.get());
  }

  public static void clear() {
    currentUser.remove();
  }

  public static ROLE getUserRole() {
    //    check user login or not
    checkLogin();
    return currentUser.get().getRole();
  }

  public static Long getUserId() {
    //    check user login or not
    checkLogin();
    return currentUser.get().getId();
  }

  public static String getUserEmail() {
    //    check user login or not
    checkLogin();
    return currentUser.get().getEmail();
  }

  public static void checkLogin() {
    if (Objects.isNull(currentUser.get())) {
      throw new BadRequestException("You are not logged in can't get user; note : UserContext");
    }
  }
}

package com.sila.share;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Utils {
  public static <T> void setValueSafe(T value, Consumer<T> setter) {
    if (value != null) {
      setter.accept(value);
    }
  }

  public static String capitalize(String text) {
    if (text == null || text.isEmpty()) return text;
    return text.substring(0, 1).toUpperCase() + text.substring(1);
  }

  public static String singular(String word) {
    if (word.endsWith("s")) {
      return word.substring(0, word.length() - 1);
    }
    return word;
  }

  public static String extractConstraintName(String message) {

    if (message == null) {
      return null;
    }

    Pattern pattern = Pattern.compile("CONSTRAINT [`\"]?(\\w+)[`\"]?");
    Matcher matcher = pattern.matcher(message);

    if (matcher.find()) {
      return matcher.group(1);
    }

    return null;
  }
}

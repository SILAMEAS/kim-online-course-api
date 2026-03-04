package com.sila.share.core.crud;


import java.util.Arrays;

public interface BaseSortEnum {

  String getProperty();

  String getParameter();

  static <E extends Enum<E> & BaseSortEnum> E fromParameter(Class<E> enumType, String parameter) {
    if (parameter == null || parameter.isBlank()) {
      return null;
    }
    return Arrays.stream(enumType.getEnumConstants())
        .filter(
            e ->
                e.name().equalsIgnoreCase(parameter)
                    || e.getParameter().equalsIgnoreCase(parameter))
        .findFirst()
        .orElseThrow(
            () -> new java.security.InvalidParameterException("Invalid sort field: " + parameter));
  }
}

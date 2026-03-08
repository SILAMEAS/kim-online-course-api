package com.sila.config.jwt;

public class JwtConstant {
  public static final String SECRET_KEY = "xxxxx-sila-kim-sour-xxxx-00993-3454-23432";
  public static final String JWT_HEADER = "Authorization";
  //  think as milliseconds
  public static final long ACCESS_TOKEN_EXPIRATION = 60 * 60 * 1000L; // 1 hour
  public static final long REFRESH_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000L; // 1 day
}

package com.sila.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtTokenValidator extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader(JwtConstant.JWT_HEADER);

    if (header != null && header.startsWith("Bearer ")) {
      String jwt = header.substring(7);

      try {
        SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

        Claims claims =
            Jwts.parserBuilder()
                .setSigningKey(key)
                .setAllowedClockSkewSeconds(60) // ✅ allow 60s clock skew
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        String email = String.valueOf(claims.get("email"));
        String authorities = String.valueOf(claims.get("authorities"));
        List<GrantedAuthority> auth =
            AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, auth);
        SecurityContextHolder.getContext().setAuthentication(authentication);

      } catch (ExpiredJwtException ex) {
        // Token expired → respond 401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response
            .getWriter()
            .write(
                """
                        {
                          "status":401,
                          "message":"JWT token expired"
                        }
                        """);
        return; // stop filter chain

      } catch (JwtException | IllegalArgumentException ex) {
        // Invalid token → respond 401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response
            .getWriter()
            .write(
                """
                        {
                          "status":401,
                          "message":"Invalid JWT token"
                        }
                        """);
        return; // stop filter chain
      }
    }

    // Continue filter chain if token is valid or not present
    filterChain.doFilter(request, response);
  }
}

package com.sila.modules.profile.model;

import com.sila.share.core.entity.AbstractAuditable;
import com.sila.share.enums.ROLE;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(
    name = "users",
    uniqueConstraints = {@UniqueConstraint(name = "uk_user_email", columnNames = "email")})
public class User extends AbstractAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Builder.Default private ROLE role = ROLE.STUDENT;
}

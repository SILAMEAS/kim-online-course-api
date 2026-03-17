package com.sila.modules.profile.model;

import com.sila.modules.image.model.Image;
import com.sila.share.core.entity.AbstractAuditable;
import com.sila.share.enums.ROLE;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a system user, including students and instructors.
 *
 * <p>Tracks basic information like name, email, password, and role.
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = {@UniqueConstraint(name = "uk_user_email", columnNames = "email")})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AbstractAuditable {

  /** Primary key for the user. Auto-generated. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** First name of the user. Cannot be null. */
  @Column(name = "first_name", nullable = false)
  private String firstName;

  /** Last name of the user. Cannot be null. */
  @Column(name = "last_name", nullable = false)
  private String lastName;

  /** Email of the user. Must be unique and cannot be null. */
  @Column(name = "email", nullable = false, unique = true)
  private String email;

  /** Encrypted password. Cannot be null. */
  @Column(name = "password", nullable = false)
  private String password;

  /** Role of the user. Defaults to STUDENT. */
  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 20)
  private ROLE role = ROLE.STUDENT;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "image_id",
      referencedColumnName = "id",
      nullable = true,
      foreignKey = @ForeignKey(name = "fk_users_images"))
  private Image image;
}

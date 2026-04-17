package com.sila.modules.profile.model;

import com.sila.modules.course.model.Course;
import com.sila.share.core.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "wishlists",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_user_course_wishlist",
          columnNames = {"user_id", "course_id"})
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wishlist extends AbstractAuditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 🔥 Many wishlist items belong to one user
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_wishlist_user"))
  private User user;

  // 🔥 Many wishlist items belong to one course
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(
      name = "course_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_wishlist_course"))
  private Course course;
}

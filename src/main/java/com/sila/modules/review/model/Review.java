package com.sila.modules.review.model;

import com.sila.modules.course.model.Course;
import com.sila.modules.payment.model.Payment;
import com.sila.modules.profile.model.User;
import com.sila.share.core.entity.AbstractAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a student's enrollment in a course.
 *
 * <p>Each enrollment links a {@link User} with a {@link Course} and tracks the associated {@link
 * Payment} and the {@link }.
 */
@Entity
@Table(name = "reviews")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Review extends AbstractAuditable {

  /** Primary key for the enrollment. Auto-generated. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "rating", nullable = false)
  private Integer rating;

  @Column(name = "title")
  private String title;

  @Column(name = "comment")
  private String comment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_reviews_users"))
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "course_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_reviews_courses"))
  private Course course;
}

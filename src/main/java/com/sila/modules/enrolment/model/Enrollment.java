package com.sila.modules.enrolment.model;

import com.sila.modules.course.model.Course;
import com.sila.modules.enrolment.Enum.EnrollmentStatus;
import com.sila.modules.payment.model.Payment;
import com.sila.modules.profile.model.User;
import com.sila.share.core.entity.AbstractAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Payment} and the {@link EnrollmentStatus}.
 */
@Entity
@Table(name = "enrollments")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Enrollment extends AbstractAuditable {

  /** Primary key for the enrollment. Auto-generated. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Status of the enrollment, e.g., ACTIVE, CANCELLED. Cannot be null. */
  @Column(name = "status", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private EnrollmentStatus status;

  /**
   * The user who is enrolled in the course.
   *
   * <p>References the {@link User} entity. Cannot be null. Uses lazy loading.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_id",
      referencedColumnName = "id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_enrolments_users"))
  private User user;

  /**
   * The course associated with this enrollment.
   *
   * <p>References the {@link Course} entity. Cannot be null. Uses lazy loading.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "course_id",
      referencedColumnName = "id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_enrolments_courses"))
  private Course course;

  /**
   * The payment record associated with this enrollment.
   *
   * <p>References the {@link Payment} entity. Cannot be null. Uses lazy loading.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "payment_id",
      referencedColumnName = "id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_enrolments_payments"))
  private Payment payment;
}

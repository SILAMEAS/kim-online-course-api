package com.sila.modules.payment.model;

import com.sila.modules.course.model.Course;
import com.sila.modules.payment.Enum.PaymentStatus;
import com.sila.modules.profile.model.User;
import com.sila.share.core.entity.AbstractAuditable;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a payment made by a user for a course.
 *
 * <p>Tracks the payment status, proof image, approval info, and links to the {@link User} and
 * {@link Course} entities.
 */
@Entity
@Table(name = "payments")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Payment extends AbstractAuditable {

  /** Primary key for the payment. Auto-generated. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Status of the payment (e.g., PENDING, DONE). Cannot be null. */
  @Column(name = "status", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  /** Optional proof of payment (image URL or path). */
  @Column(name = "proof_image")
  private String proofImage;

  /** Amount paid. Cannot be null. */
  @Column(name = "amount", nullable = false)
  private Double amount;

  /** ID of the user who approved the payment, if any. */
  @Column(name = "approved_by")
  private Long approvedBy;

  /** Timestamp when the payment was approved, if any. */
  @Column(name = "approved_at")
  private Instant approvedAt;

  /**
   * The course associated with this payment.
   *
   * <p>Cannot be null. Uses lazy loading for performance.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "course_id",
      referencedColumnName = "id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_payments_courses"))
  private Course course;

  /**
   * The user who made the payment.
   *
   * <p>Cannot be null. Uses lazy loading for performance.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_id",
      referencedColumnName = "id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_payments_users"))
  private User user;

  /**
   * Lifecycle callback before creation.
   *
   * <p>Sets default status to PENDING and clears approval info.
   */
  @Override
  public void onPreCreated() {
    this.status = PaymentStatus.PENDING;
    this.approvedBy = null;
    this.approvedAt = null;
  }
}

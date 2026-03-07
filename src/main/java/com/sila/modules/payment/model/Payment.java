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

@Entity
@Table(name = "payments")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Payment extends AbstractAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "status", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  @Column(name = "proof_image")
  private String proofImage;

  @Column(name = "amount", nullable = false)
  private Double amount;

  @Column(name = "approved_by", nullable = false)
  private Long approvedBy;

  @Column(name = "approved_at", nullable = false)
  private Instant approvedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "course_id",
      referencedColumnName = "id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_payments_courses"))
  private Course course;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_id",
      referencedColumnName = "id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_payments_users"))
  private User user;
}

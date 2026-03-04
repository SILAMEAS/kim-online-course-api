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

@Entity
@Table(name = "enrollments")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Enrollment extends AbstractAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "status", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private EnrollmentStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_id",
      referencedColumnName = "id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_enrolments_users"))
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "course_id",
      referencedColumnName = "id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_enrolments_courses"))
  private Course course;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "payment_id",
      referencedColumnName = "id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_enrolments_payments"))
  private Payment payment;
}

package com.sila.modules.course.model;

import com.sila.modules.profile.model.User;
import com.sila.share.core.entity.AbstractAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "courses",
    uniqueConstraints = {@UniqueConstraint(name = "uk_course_title", columnNames = "title")},
    indexes = {
      @Index(name = "idx_course_title", columnList = "title"),
      @Index(name = "idx_course_instructor", columnList = "instructor_id")
    })
public class Course extends AbstractAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title", nullable = false, unique = true)
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "price", nullable = false)
  private Double price;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "instructor_id",
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_courses_users"),
      nullable = false)
  private User instructor;
}

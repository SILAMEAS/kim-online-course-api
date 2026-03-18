package com.sila.modules.course.model;

import com.sila.modules.image.model.Image;
import com.sila.modules.profile.model.User;
import com.sila.share.core.entity.AbstractAuditable;
import com.sila.share.enums.CourseStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a course in the system.
 *
 * <p>A course contains basic information like title, description, price, and the instructor
 * responsible for the course.
 *
 * <p>Each course is uniquely identified by its title.
 */
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

  /** Primary key for the course entity. Auto-generated. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Title of the course. Must be unique and non-null. */
  @Column(name = "title", nullable = false, unique = true)
  private String title;

  /** Description of the course. Optional. */
  @Column(name = "description")
  private String description;

  /** Price of the course. Cannot be null. */
  @Column(name = "price", nullable = false)
  private Double price;

  /** Price of the course. Cannot be null. */
  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private CourseStatus status;

  /**
   * Instructor associated with the course.
   *
   * <p>References the {@link User} entity. Cannot be null. Uses lazy loading for performance.
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(
      name = "instructor_id",
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_courses_users"),
      nullable = false)
  private User instructor;

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinColumn(
      name = "image_id",
      referencedColumnName = "id",
      nullable = true,
      foreignKey = @ForeignKey(name = "fk_course_images"))
  private Image image;
  
}

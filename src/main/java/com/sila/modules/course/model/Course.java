package com.sila.modules.course.model;

import com.sila.modules.course.Enum.CourseStatus;
import com.sila.modules.course.Enum.LevelStatus;
import com.sila.modules.image.model.Image;
import com.sila.modules.profile.model.User;
import com.sila.share.core.entity.AbstractAuditable;
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

  /** Price of the course. Cannot be null. */
  @Column(name = "level")
  @Enumerated(EnumType.STRING)
  private LevelStatus level;

  /** Price of the course. Cannot be null. */
  @Column(name = "rating", length = 1)
  private Double rating;

  /** Price of the course. Cannot be null. */
  @Column(name = "reviews_count")
  private Long reviewsCount;

  /** Price of the course. Cannot be null. */
  @Column(name = "duration")
  private Double duration;

  /** Price of the course. Cannot be null. */
  @Column(name = "students_count")
  private Integer studentsCount;

  /**
   * Instructor associated with the course.
   *
   * <p>References the {@link User} entity. Cannot be null. Uses lazy loading for performance.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "instructor_id",
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_courses_users"),
      nullable = false)
  private User instructor;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(
      name = "image_id",
      referencedColumnName = "id",
      nullable = true,
      foreignKey = @ForeignKey(name = "fk_course_images"))
  private Image image;

  @ManyToOne(fetch = FetchType.EAGER) // Lazy is generally better for performance
  @JoinColumn(
      name = "category_id",
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_courses_categories"))
  private Category category;

  @Override
  public void onPreCreated() {
    this.studentsCount = 0;
    this.reviewsCount = 0L;
    this.rating = 0D;
    this.level = LevelStatus.BEGINNER;
    this.duration = 0.0;
  }
}

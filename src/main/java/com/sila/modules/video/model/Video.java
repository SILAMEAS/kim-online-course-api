package com.sila.modules.video.model;

import com.sila.modules.course.model.Course;
import com.sila.share.core.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents a video uploaded for a course.
 *
 * <p>Tracks metadata like title, order in the course, and the Cloudinary public ID.
 */
@Entity
@Table(name = "videos")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Video extends AbstractAuditable {

  /** Primary key for the video. Auto-generated. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Title of the video. */
  @Column(name = "title", nullable = false)
  private String title;

  /** Cloudinary public ID for the video. */
  @Column(name = "public_id", nullable = false)
  private String publicId;

  /** Optional order index to sort videos within a course. */
  @Column(name = "order_index")
  private Integer orderIndex;

  /** Course this video belongs to. Cannot be null. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "course_id",
      referencedColumnName = "id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_videos_course"))
  private Course course;
}

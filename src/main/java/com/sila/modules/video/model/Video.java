package com.sila.modules.video.model;

import com.sila.modules.course.model.Course;
import com.sila.share.core.entity.AbstractAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "videos")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Video extends AbstractAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "title")
  private String title;

  @Column(name = "public_id")
  private String publicId;

  @Column(name = "order_index")
  private Integer orderIndex;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "course_id",
      referencedColumnName = "id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_videos_course"))
  private Course course;
}

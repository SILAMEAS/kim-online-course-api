package com.sila.modules.course.model;


import com.sila.modules.courseSection.model.Section;
import com.sila.modules.profile.model.User;
import com.sila.share.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "course")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "course",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_course_title", columnNames = "title")
        }
)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CourseStatus status;

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @OneToMany(mappedBy = "course")
    private List<Section> sections;

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();


    @PrePersist
    public void prePersist() {
        this.setSections(List.of());
        this.setStatus(CourseStatus.DRAFT);
    }

}

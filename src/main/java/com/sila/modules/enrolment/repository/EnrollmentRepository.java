package com.sila.modules.enrolment.repository;

import com.sila.modules.course.model.Course;
import com.sila.modules.enrolment.Enum.EnrollmentStatus;
import com.sila.modules.enrolment.model.Enrollment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnrollmentRepository
    extends JpaRepository<Enrollment, Long>, JpaSpecificationExecutor<Enrollment> {
  boolean existsByUserIdAndCourseIdAndStatus(Long userId, Long courseId, EnrollmentStatus status);

  boolean existsByUser_IdAndCourse_Id(Long userId, Long courseId);

  boolean existsByCourse_Id(Long courseId);

  void deleteAllByCourse_Id(Long courseId);
}

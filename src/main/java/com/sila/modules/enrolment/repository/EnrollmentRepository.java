package com.sila.modules.enrolment.repository;

import com.sila.modules.enrolment.Enum.EnrollmentStatus;
import com.sila.modules.enrolment.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EnrollmentRepository
    extends JpaRepository<Enrollment, Long>, JpaSpecificationExecutor<Enrollment> {
  boolean existsByUserIdAndCourseIdAndStatus(Long userId, Long courseId, EnrollmentStatus status);
}

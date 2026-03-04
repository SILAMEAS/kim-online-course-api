package com.sila.modules.enrolment.service;

import com.sila.modules.enrolment.Enum.EnrollmentStatus;
import com.sila.modules.enrolment.model.Enrollment;
import com.sila.modules.enrolment.repository.EnrollmentRepository;
import com.sila.share.core.crud.AbstractCrudCommon;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentService extends AbstractCrudCommon<Enrollment, Long, EnrollmentRepository> {
  protected EnrollmentService(EnrollmentRepository baseRepository, ModelMapper mapper) {
    super(baseRepository, mapper);
  }

  public boolean canAccess(Long userId, Long courseId) {
    return this.baseRepository.existsByUserIdAndCourseIdAndStatus(userId, courseId, EnrollmentStatus.ACTIVE);
  }
}

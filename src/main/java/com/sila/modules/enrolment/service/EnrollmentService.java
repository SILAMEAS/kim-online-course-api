package com.sila.modules.enrolment.service;

import com.sila.modules.enrolment.Enum.EnrollmentStatus;
import com.sila.modules.enrolment.dto.EnrollmentResponse;
import com.sila.modules.enrolment.model.Enrollment;
import com.sila.modules.enrolment.repository.EnrollmentRepository;
import com.sila.modules.payment.model.Payment;
import com.sila.share.core.crud.AbstractCrudCommon;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnrollmentService extends AbstractCrudCommon<Enrollment, Long, EnrollmentRepository> {
  protected EnrollmentService(EnrollmentRepository baseRepository, ModelMapper mapper) {
    super(baseRepository, mapper);
  }

  public boolean canAccess(Long userId, Long courseId) {
    return this.baseRepository.existsByUser_IdAndCourse_Id(userId, courseId);
  }

  @Transactional
  public EnrollmentResponse createEnrollment(Payment payment) {
    Enrollment enroll = new Enrollment();
    enroll.setUser(payment.getUser());
    enroll.setCourse(payment.getCourse());
    enroll.setPayment(payment);
    enroll.setStatus(EnrollmentStatus.ACTIVE);
    super.save(enroll);
    return this.mapper.map(enroll, EnrollmentResponse.class);
  }
}

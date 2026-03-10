package com.sila.modules.enrolment.service;

import com.sila.config.exception.NotFoundException;
import com.sila.modules.course.repository.CourseRepository;
import com.sila.modules.enrolment.Enum.EnrollmentStatus;
import com.sila.modules.enrolment.dto.EnrollmentResponse;
import com.sila.modules.enrolment.model.Enrollment;
import com.sila.modules.enrolment.repository.EnrollmentRepository;
import com.sila.modules.enrolment.spec.EnrollmentSpec;
import com.sila.modules.payment.model.Payment;
import com.sila.share.constant.StaticMessage;
import com.sila.share.core.crud.AbstractCrudCommon;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.PaginationRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnrollmentService extends AbstractCrudCommon<Enrollment, Long, EnrollmentRepository> {

  private final CourseRepository courseRepository;

  protected EnrollmentService(
      EnrollmentRepository baseRepository, ModelMapper mapper, CourseRepository courseRepository) {
    super(baseRepository, mapper);
    this.courseRepository = courseRepository;
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

  @Transactional(readOnly = true)
  public EntityResponseHandler<EnrollmentResponse> listAllEnrollment(
      Long courseId, PaginationRequest paginationRequest) {

    final var pageable = super.toPageable(paginationRequest);

    var spec =
        EnrollmentSpec.search(paginationRequest.getSearch())
            .and(EnrollmentSpec.byOwnership())
            .and(EnrollmentSpec.byCourse(courseId));

    var enrollPages = super.findAll(spec, pageable);

    return new EntityResponseHandler<>(
        enrollPages.map(en -> mapper.map(en, EnrollmentResponse.class)));
  }

  @Transactional
  public String bulkDeleteByCourseId(Long courseId) {

    this.courseRepository
        .findById(courseId)
        .orElseThrow(() -> new NotFoundException(StaticMessage.COURSE_NOT_FOUND));

    this.baseRepository.deleteAllByCourse_Id(courseId);

    return StaticMessage.DELETE_BULK_ENROLLMENT_BY_COURSE;
  }

  //  Internal

  @Transactional(readOnly = true)
  public boolean existByCourseId(Long courseId) {

    return this.baseRepository.existsByCourse_Id(courseId);
  }
}

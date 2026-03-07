package com.sila.modules.enrolment.service;

import com.sila.config.context.UserContext;
import com.sila.modules.enrolment.Enum.EnrollmentStatus;
import com.sila.modules.enrolment.dto.EnrollmentResponse;
import com.sila.modules.enrolment.model.Enrollment;
import com.sila.modules.enrolment.repository.EnrollmentRepository;
import com.sila.modules.enrolment.spec.EnrollmentSpec;
import com.sila.modules.payment.model.Payment;
import com.sila.share.core.crud.AbstractCrudCommon;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.enums.ROLE;
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

  @Transactional(readOnly = true)
  public EntityResponseHandler<EnrollmentResponse> listAllEnrollment(
      PaginationRequest paginationRequest) {

    final var pageable =
        super.toPageable(
            paginationRequest.getPage(),
            paginationRequest.getLimit(),
            paginationRequest.getSortBy(),
            String.valueOf(paginationRequest.getSortOrder()));

    var spec = EnrollmentSpec.search(paginationRequest.getSearch());

    if (UserContext.getUserRole() != ROLE.ADMIN) {
      spec = spec.and(EnrollmentSpec.byUserId(UserContext.getUserId()));
    }

    var enrollPages = super.findAll(spec, pageable);

    return new EntityResponseHandler<>(
        enrollPages.map(en -> mapper.map(en, EnrollmentResponse.class)));
  }
}

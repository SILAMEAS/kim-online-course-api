package com.sila.modules.payment.service;

import com.sila.config.context.UserContext;
import com.sila.config.exception.BadRequestException;
import com.sila.config.exception.NotFoundException;
import com.sila.modules.course.model.Course;
import com.sila.modules.course.repository.CourseRepository;
import com.sila.modules.enrolment.dto.EnrollmentResponse;
import com.sila.modules.enrolment.service.EnrollmentService;
import com.sila.modules.payment.Enum.PaymentStatus;
import com.sila.modules.payment.dto.ListPaymentResponse;
import com.sila.modules.payment.dto.PaymentResponse;
import com.sila.modules.payment.model.Payment;
import com.sila.modules.payment.repository.PaymentRepository;
import com.sila.modules.payment.spec.PaymentSpec;
import com.sila.modules.profile.model.User;
import com.sila.modules.profile.repository.UserRepository;
import com.sila.share.core.crud.AbstractCrudCommon;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.enums.ROLE;
import java.time.Instant;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService extends AbstractCrudCommon<Payment, Long, PaymentRepository> {
  private final CourseRepository courseRepository;
  private final EnrollmentService enrollmentService;
  private final UserRepository userRepository;

  protected PaymentService(
      PaymentRepository baseRepository,
      ModelMapper mapper,
      CourseRepository courseRepository,
      EnrollmentService enrollmentService,
      UserRepository userRepository) {
    super(baseRepository, mapper);
    this.courseRepository = courseRepository;
    this.enrollmentService = enrollmentService;
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public Payment findById(@NonNull Long paymentId) {
    return super.findById(paymentId);
  }

  @Transactional(readOnly = true)
  public EntityResponseHandler<ListPaymentResponse> listPayments(
      PaginationRequest paginationRequest) {
    final var pageable =
        super.toPageable(
            paginationRequest.getPage(),
            paginationRequest.getLimit(),
            paginationRequest.getSortBy(),
            String.valueOf(paginationRequest.getSortOrder()));

    var spec = PaymentSpec.search(paginationRequest.getSearch());

    if (UserContext.getUserRole() != ROLE.ADMIN) {
      spec = spec.and(PaymentSpec.byUserId(UserContext.getUserId()));
    }

    Page<Payment> pagePayments = super.findAll(spec, pageable);
    return new EntityResponseHandler<>(
        pagePayments.map(pt -> this.mapper.map(pt, ListPaymentResponse.class)));
  }

  @Transactional
  public EnrollmentResponse approvePayment(Long id) {

    //    Modify Payment
    Payment payment =
        this.baseRepository
            .findWithUser(id)
            .orElseThrow(() -> new RuntimeException("Payment not found"));
    payment.setApprovedAt(Instant.now());
    payment.setApprovedBy(UserContext.getUserId());
    payment.setStatus(PaymentStatus.DONE);

    //    Enrollment
    return this.enrollmentService.createEnrollment(payment);
  }

  @Transactional
  public PaymentResponse createPayments(Long courseId) {

    Course course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found"));

    User user = UserContext.getUser();

    if (super.baseRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
      throw new BadRequestException("Payment already submitted for this course");
    }

    Payment payment = new Payment();
    payment.setUser(user);
    payment.setCourse(course);
    payment.setAmount(course.getPrice());

    super.save(payment);

    return mapper.map(payment, PaymentResponse.class);
  }
}

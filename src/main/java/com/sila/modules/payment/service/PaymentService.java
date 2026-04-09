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
import com.sila.share.core.crud.AbstractCrudCommon;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.PaginationRequest;
import java.time.Instant;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing course payments.
 *
 * <p>This service handles:
 *
 * <ul>
 *   <li>Retrieving payment details.
 *   <li>Listing payments with pagination and search.
 *   <li>Approving payments and creating enrollments.
 *   <li>Creating new payments for courses.
 * </ul>
 */
@Service
public class PaymentService extends AbstractCrudCommon<Payment, Long, PaymentRepository> {

  private final CourseRepository courseRepository;
  private final EnrollmentService enrollmentService;

  protected PaymentService(
      PaymentRepository baseRepository,
      ModelMapper mapper,
      CourseRepository courseRepository,
      EnrollmentService enrollmentService) {
    super(baseRepository, mapper);
    this.courseRepository = courseRepository;
    this.enrollmentService = enrollmentService;
  }

  /**
   * Retrieves payment details by ID.
   *
   * @param paymentId ID of the payment (cannot be null)
   * @return Payment entity
   * @throws RuntimeException if payment not found
   */
  @Transactional(readOnly = true)
  public Payment getDetail(@NonNull Long paymentId) {
    return super.findById(paymentId);
  }

  /**
   * Lists all payments for the current user with pagination and optional search.
   *
   * <p>Filters applied:
   *
   * <ul>
   *   <li>Search term from PaginationRequest
   *   <li>Ownership filter: only payments for the current user
   * </ul>
   *
   * @param paginationRequest PaginationRequest containing page, limit, sort, and search term.
   * @return EntityResponseHandler containing paginated ListPaymentResponse DTOs.
   */
  @Transactional(readOnly = true)
  public EntityResponseHandler<ListPaymentResponse> listPayments(
      PaginationRequest paginationRequest) {

    final var pageable =
        super.toPageable(
            paginationRequest.getPage(),
            paginationRequest.getLimit(),
            paginationRequest.getSortBy(),
            String.valueOf(paginationRequest.getSortOrder()));

    var spec = PaymentSpec.search(paginationRequest.getSearch()).and(PaymentSpec.byOwnership());

    Page<Payment> pagePayments = super.findAll(spec, pageable);
    return new EntityResponseHandler<>(
        pagePayments.map(pt -> this.mapper.map(pt, ListPaymentResponse.class)));
  }

  /**
   * Approves a payment and creates enrollment for the associated course.
   *
   * <p>Steps:
   *
   * <ol>
   *   <li>Sets approvedAt timestamp and approvedBy user ID.
   *   <li>Updates payment status to DONE.
   *   <li>Creates enrollment for the user in the course.
   * </ol>
   *
   * @param id ID of the payment to approve
   * @return EnrollmentResponse representing the newly created enrollment
   * @throws RuntimeException if payment not found
   */
  @Transactional
  public EnrollmentResponse approvePayment(Long id) {

    Payment payment =
        this.baseRepository
            .findWithUser(id)
            .orElseThrow(() -> new RuntimeException("Payment not found"));
    payment.setApprovedAt(Instant.now());
    payment.setApprovedBy(UserContext.getUserId());
    payment.setStatus(PaymentStatus.DONE);

    return this.enrollmentService.createEnrollment(payment);
  }

  /**
   * Creates a new payment for a course by the current user.
   *
   * <p>Validations:
   *
   * <ul>
   *   <li>Course must exist.
   *   <li>User cannot submit duplicate payment for the same course.
   * </ul>
   *
   * @param courseId ID of the course to pay for
   * @return PaymentResponse representing the newly created payment
   * @throws NotFoundException if the course does not exist
   * @throws BadRequestException if a payment already exists for this course by the user
   */
  @Transactional
  public PaymentResponse createPayments(Long courseId) {

    Course course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found"));

    User user = UserContext.getUser();

    if (super.baseRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
      throw new BadRequestException("Payment already submitted for this course waiting admin to confirmation");
    }

    Payment payment = new Payment();
    payment.setUser(user);
    payment.setCourse(course);
    payment.setAmount(course.getPrice());

    super.save(payment);

    return mapper.map(payment, PaymentResponse.class);
  }
}

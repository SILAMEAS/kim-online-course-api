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
import com.sila.share.core.pagination.ResponsePaginationHandler;
import com.sila.share.core.pagination.PaginationRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing course enrollments.
 *
 * <p>This service provides functionalities to:
 *
 * <ul>
 *   <li>Check user access to a course.
 *   <li>Create enrollments based on payment.
 *   <li>List enrollments with pagination and search filters.
 *   <li>Bulk delete enrollments by course.
 *   <li>Check if enrollments exist for a specific course.
 * </ul>
 */
@Service
public class EnrollmentService extends AbstractCrudCommon<Enrollment, Long, EnrollmentRepository> {

  private final CourseRepository courseRepository;

  protected EnrollmentService(
      EnrollmentRepository baseRepository, ModelMapper mapper, CourseRepository courseRepository) {
    super(baseRepository, mapper);
    this.courseRepository = courseRepository;
  }

  /**
   * Checks if a user is enrolled in a specific course.
   *
   * @param userId ID of the user.
   * @param courseId ID of the course.
   * @return true if the user has access to the course, false otherwise.
   */
  public boolean canAccess(Long userId, Long courseId) {
    return this.baseRepository.existsByUser_IdAndCourse_Id(userId, courseId);
  }

  /**
   * Creates a new enrollment for a user based on a successful payment.
   *
   * <p>The enrollment status will be set to ACTIVE by default.
   *
   * @param payment Payment entity containing user and course information.
   * @return EnrollmentResponse DTO representing the newly created enrollment.
   */
  @Transactional
  public EnrollmentResponse createEnrollment(Payment payment) {
    Enrollment enroll = new Enrollment();
    enroll.setUser(payment.getUser());
    enroll.setCourse(payment.getCourse());
    enroll.setPayment(payment);
    enroll.setStatus(EnrollmentStatus.ACTIVE);
    super.save(enroll);

    // Update studentsCount in Course (+1)
    var course = payment.getCourse();
    int currentCount = course.getStudentsCount() != null ? course.getStudentsCount() : 0;
    course.setStudentsCount(currentCount + 1);
    courseRepository.save(course);

    return this.mapper.map(enroll, EnrollmentResponse.class);
  }

  /**
   * Lists all enrollments for a specific course with pagination and optional search filters.
   *
   * <p>Filters applied:
   *
   * <ul>
   *   <li>Search term from PaginationRequest
   *   <li>Ownership filter (current user)
   *   <li>Course filter by courseId
   * </ul>
   *
   * @param courseId ID of the course to list enrollments for.
   * @param paginationRequest PaginationRequest containing page, size, sort, and search.
   * @return EntityResponseHandler containing paginated EnrollmentResponse DTOs.
   */
  @Transactional(readOnly = true)
  public ResponsePaginationHandler<EnrollmentResponse> listAllEnrollment(
      Long courseId, PaginationRequest paginationRequest) {

    final var pageable = super.toPageable(paginationRequest);

    var spec =
        EnrollmentSpec.search(paginationRequest.getSearch())
            .and(EnrollmentSpec.byOwnership())
            .and(EnrollmentSpec.byCourse(courseId));

    var enrollPages = super.findAll(spec, pageable);

    return new ResponsePaginationHandler<>(
        enrollPages.map(en -> mapper.map(en, EnrollmentResponse.class)));
  }

  /**
   * Bulk deletes all enrollments for a specific course.
   *
   * <p>Throws NotFoundException if the course does not exist.
   *
   * @param courseId ID of the course.
   * @return Message confirming deletion.
   */
  @Transactional
  public String bulkDeleteByCourseId(Long courseId) {
    var course = this.courseRepository
        .findById(courseId)
        .orElseThrow(() -> new NotFoundException(StaticMessage.COURSE_NOT_FOUND));

    this.baseRepository.deleteAllByCourse_Id(courseId);

    // After bulk delete, reset the count to 0
    course.setStudentsCount(0);
    this.courseRepository.save(course);

    return StaticMessage.DELETE_BULK_ENROLLMENT_BY_COURSE;
  }

  // Internal

  /**
   * Checks if there is any enrollment for the specified course.
   *
   * @param courseId ID of the course.
   * @return true if there are existing enrollments, false otherwise.
   */
  @Transactional(readOnly = true)
  public boolean existByCourseId(Long courseId) {
    return this.baseRepository.existsByCourse_Id(courseId);
  }
}

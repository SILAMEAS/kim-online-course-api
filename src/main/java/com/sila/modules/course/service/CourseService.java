package com.sila.modules.course.service;

import com.sila.config.exception.BadRequestException;
import com.sila.modules.course.dto.CourseDetailResponse;
import com.sila.modules.course.dto.CourseResponse;
import com.sila.modules.course.dto.CreateCourseRequest;
import com.sila.modules.course.dto.UpdateCourseRequest;
import com.sila.modules.course.mapping.CourseMapping;
import com.sila.modules.course.model.Course;
import com.sila.modules.course.repository.CourseRepository;
import com.sila.modules.course.spec.CourseSpec;
import com.sila.modules.enrolment.service.EnrollmentService;
import com.sila.modules.image.Enum.CloudinaryFolder;
import com.sila.modules.image.service.ImageService;
import com.sila.modules.profile.service.UserService;
import com.sila.modules.video.service.VideoService;
import com.sila.share.Utils;
import com.sila.share.constant.StaticMessage;
import com.sila.share.core.crud.AbstractCrudCommon;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.PaginationRequest;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing courses.
 *
 * <p>Provides CRUD operations for courses, including:
 *
 * <ul>
 *   <li>Listing courses with pagination and search.
 *   <li>Creating a new course with instructor assignment.
 *   <li>Retrieving detailed course information.
 *   <li>Deleting a course (with validation for existing enrollments).
 * </ul>
 */
@Service
public class CourseService extends AbstractCrudCommon<Course, Long, CourseRepository> {

  private final UserService userService;
  private final VideoService videoService;
  private final EnrollmentService enrollmentService;
  private final ImageService imageService;
  private final CourseMapping courseMapping;

  protected CourseService(
      CourseRepository baseRepository,
      ModelMapper mapper,
      UserService userService,
      VideoService videoService,
      EnrollmentService enrollmentService,
      ImageService imageService,
      CourseMapping courseMapping) {
    super(baseRepository, mapper);
    this.userService = userService;
    this.videoService = videoService;
    this.enrollmentService = enrollmentService;
    this.imageService = imageService;
    this.courseMapping = courseMapping;
  }

  /**
   * List all courses with pagination and optional search.
   *
   * @param request PaginationRequest containing page number, size, sortBy, sortOrder, and search
   *     term.
   * @return Paginated response of CourseResponse DTOs.
   */
  @Transactional(readOnly = true)
  public EntityResponseHandler<CourseResponse> lists(PaginationRequest request) {
    final var pageable =
        super.toPageable(
            request.getPage(),
            request.getLimit(),
            request.getSortBy(),
            String.valueOf(request.getSortOrder()));
    final var spec = CourseSpec.search(request.getSearch());
    Page<Course> courses = super.findAll(spec, pageable);
    return new EntityResponseHandler<>(courses.map(courseMapping::mapToCourseResponse));
  }

  /**
   * Create a new course.
   *
   * <p>This method assigns the course to an instructor and persists it in the database.
   *
   * @param request CreateCourseRequest containing title, description, price, and instructorId.
   * @return CourseResponse DTO of the newly created course.
   */
  @Transactional
  public CourseResponse create(CreateCourseRequest request) {
    Course course = new Course();
    course.setStatus(request.getStatus());
    course.setTitle(request.getTitle());
    course.setDescription(request.getDescription());
    course.setPrice(request.getPrice());
    course.setImage(this.imageService.createImage(request.getFile(), CloudinaryFolder.COURSE));
    var instructor = userService.getById(request.getInstructorId());
    course.setInstructor(instructor);

    super.save(course);
    return this.mapper.map(course, CourseResponse.class);
  }

  /** Update course. */
  @Transactional
  public CourseResponse update(Long courseId, UpdateCourseRequest request) {

    var course = super.findById(courseId);
    //    general information
    Utils.setValueSafe(request.getTitle(), course::setTitle);
    Utils.setValueSafe(request.getDescription(), course::setDescription);
    Utils.setValueSafe(request.getPrice(), course::setPrice);
    Utils.setValueSafe(request.getStatus(), course::setStatus);
    //    update instructor
    if (request.getInstructorId() != null) {
      var instructor = this.userService.getById(request.getInstructorId());
      Utils.setValueSafe(instructor, course::setInstructor);
    }
    //    process update image
    var fileUpload = request.getFile();
    var courseImage = course.getImage();
    if (fileUpload != null) {
      if (courseImage == null) {
        course.setImage(this.imageService.createImage(fileUpload, CloudinaryFolder.COURSE));
      }
      course.setImage(
          this.imageService.updateImage(course.getImage(), fileUpload, CloudinaryFolder.COURSE));
    }
    //    save course after update
    super.save(course);
    return this.mapper.map(course, CourseResponse.class);
  }

  /**
   * Retrieve detailed information for a specific course.
   *
   * @param courseId ID of the course to fetch.
   * @return CourseDetailResponse DTO including course details and instructor info.
   */
  @Transactional(readOnly = true)
  public CourseDetailResponse detail(Long courseId) {
    var course = super.findById(courseId);
    return courseMapping.mapToCourseDetailResponse(course);
  }

  /**
   * Delete a course by its ID.
   *
   * <p>Validations:
   *
   * <ul>
   *   <li>Cannot delete a course if it has existing enrollments.
   *   <li>All videos associated with the course will also be deleted.
   * </ul>
   *
   * @param courseId ID of the course to delete.
   * @throws BadRequestException if course has enrolled students.
   */
  @Transactional
  public void deleteByCourseId(Long courseId) {
    if (this.enrollmentService.existByCourseId(courseId)) {
      throw new BadRequestException(StaticMessage.COURSE_HAS_ENROLLMENT);
    }

    // Ensure the course exists
    super.findById(courseId);

    // Delete all videos associated with this course
    videoService.deleteAllVideoInCourse(courseId);

    // Delete the course from the database
    super.deleteById(courseId);
  }
}

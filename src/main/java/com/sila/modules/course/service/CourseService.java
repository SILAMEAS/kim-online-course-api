package com.sila.modules.course.service;

import com.sila.modules.course.dto.CourseDetailResponse;
import com.sila.modules.course.dto.CourseResponse;
import com.sila.modules.course.dto.CreateCourseRequest;
import com.sila.modules.course.model.Course;
import com.sila.modules.course.repository.CourseRepository;
import com.sila.modules.course.spec.CourseSpec;
import com.sila.modules.profile.service.UserService;
import com.sila.modules.video.repository.VideoRepository;
import com.sila.modules.video.service.CloudinaryService;
import com.sila.modules.video.service.VideoService;
import com.sila.share.core.crud.AbstractCrudCommon;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.PaginationRequest;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseService extends AbstractCrudCommon<Course, Long, CourseRepository> {
  final UserService userService;
  final VideoRepository videoRepository;
  final CloudinaryService cloudinaryService;
  final VideoService videoService;

  protected CourseService(
      CourseRepository baseRepository,
      ModelMapper mapper,
      UserService userService,
      VideoRepository videoRepository,
      CloudinaryService cloudinaryService,
      VideoService videoService) {
    super(baseRepository, mapper);
    this.userService = userService;
    this.videoRepository = videoRepository;
    this.cloudinaryService = cloudinaryService;
    this.videoService = videoService;
  }

  /** List Of Courses */
  @Transactional(readOnly = true)
  public EntityResponseHandler<CourseResponse> listCourse(PaginationRequest request) {
    // create pageable from request
    final var pageable =
        super.toPageable(
            request.getPage(),
            request.getLimit(),
            request.getSortBy(),
            String.valueOf(request.getSortOrder()));
    // spec for search, find and filter
    final var spec = CourseSpec.search(request.getSearch());
    // convert from entity to dto
    Page<Course> courses = super.findAll(spec, pageable);

    var coursesNew = courses.map(c -> this.mapper.map(c, CourseResponse.class));

    return new EntityResponseHandler<>(coursesNew);
  }

  /** Create Course */
  @Transactional
  public CourseResponse createCourse(CreateCourseRequest request) {

    Course course = new Course();
    course.setTitle(request.getTitle());
    course.setDescription(request.getDescription());
    course.setPrice(request.getPrice());
    var instructor = userService.getById(request.getInstructorId());
    course.setInstructor(instructor);

    super.save(course);

    return this.mapper.map(course, CourseResponse.class);
  }

  /** Get Course Detail */
  @Transactional(readOnly = true)
  public CourseDetailResponse courseDetail(Long courseId) {
    return this.mapper.map(super.findById(courseId), CourseDetailResponse.class);
  }

  /** Delete Course */
  @Transactional
  public void deleteCourse(Long courseId) {
    //    find course
    super.findById(courseId);
    //    delete all video in course
    videoService.deleteAllVideoInCourse(courseId);
    //    delete course from db
    super.deleteById(courseId);
  }
}

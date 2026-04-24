package com.sila.modules.video.service;

import com.sila.config.context.UserContext;
import com.sila.config.exception.AccessDeniedException;
import com.sila.config.exception.NotFoundException;
import com.sila.modules.course.model.Course;
import com.sila.modules.course.repository.CourseRepository;
import com.sila.modules.enrolment.service.EnrollmentService;
import com.sila.modules.profile.model.User;
import com.sila.modules.video.dto.UpdateVideoRequest;
import com.sila.modules.video.dto.VideoListResponse;
import com.sila.modules.video.dto.VideoPaginationRequest;
import com.sila.modules.video.model.Video;
import com.sila.modules.video.repository.VideoRepository;
import com.sila.modules.video.spec.VideoSpec;
import com.sila.share.Utils;
import com.sila.share.constant.StaticMessage;
import com.sila.share.core.crud.AbstractCrudCommon;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.core.pagination.ResponsePaginationHandler;
import com.sila.share.enums.ROLE;
import java.util.List;
import java.util.Map;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for managing videos.
 *
 * <p>This service handles:
 *
 * <ul>
 *   <li>Listing videos in a course or globally
 *   <li>Uploading, updating, watching, and deleting videos
 *   <li>Checking student enrollment for access control
 *   <li>Bulk deletion of videos for a course
 * </ul>
 */
@Service
public class VideoService extends AbstractCrudCommon<Video, Long, VideoRepository> {

  final EnrollmentService enrollmentService;
  private final CourseRepository courseRepository;
  private final VideoServiceCloudinary videoServiceCloudinary;

  protected VideoService(
      VideoRepository baseRepository,
      ModelMapper mapper,
      EnrollmentService enrollmentService,
      CourseRepository courseRepository,
      VideoServiceCloudinary videoServiceCloudinary) {
    super(baseRepository, mapper);
    this.enrollmentService = enrollmentService;
    this.courseRepository = courseRepository;
    this.videoServiceCloudinary = videoServiceCloudinary;
  }

  /**
   * Retrieves paginated list of videos for a specific course.
   *
   * @param courseId ID of the course
   * @param paginationRequest Pagination parameters and search term
   * @return Paginated list of VideoListResponse
   * @throws AccessDeniedException if the user is not enrolled and not ADMIN
   */
  @Transactional(readOnly = true)
  public ResponsePaginationHandler<VideoListResponse> getVideosInCourse(
      Long courseId, PaginationRequest paginationRequest) {

    if (!this.enrollmentService.canAccess(UserContext.getUserId(), courseId)
        && UserContext.getUserRole() != ROLE.ADMIN) {
      throw new AccessDeniedException("Access denied");
    }

    var pageable =
        super.toPageable(
            paginationRequest.getPage(),
            paginationRequest.getLimit(),
            paginationRequest.getSortBy(),
            String.valueOf(paginationRequest.getSortOrder()));
    var spec = VideoSpec.search(paginationRequest.getSearch()).and(VideoSpec.byCourseId(courseId));
    final var videoPage = this.baseRepository.findAll(spec, pageable);
    final var videos = videoPage.map(vd -> mapper.map(vd, VideoListResponse.class));
    return new ResponsePaginationHandler<>(videos);
  }

  /**
   * Retrieves all videos globally with pagination and search filters.
   *
   * @param paginationRequest Pagination parameters and search term
   * @return Paginated list of VideoListResponse
   */
  @Transactional(readOnly = true)
  public ResponsePaginationHandler<VideoListResponse> getAllVideos(
      VideoPaginationRequest paginationRequest) {
    var pageable =
        super.toPageable(
            paginationRequest.getPage(),
            paginationRequest.getLimit(),
            paginationRequest.getSortBy(),
            String.valueOf(paginationRequest.getSortOrder()));
    var spec = VideoSpec.search(paginationRequest.getSearch());
    if (paginationRequest.getCourseId() != null) {
      spec = spec.and(VideoSpec.byCourseId(paginationRequest.getCourseId()));
    }
    final var videoPage = super.findAll(spec, pageable);
    final var videos = videoPage.map(vd -> mapper.map(vd, VideoListResponse.class));
    return new ResponsePaginationHandler<>(videos);
  }

  /**
   * Uploads a new video to a specific course.
   *
   * @param courseId ID of the course
   * @param title Title of the video
   * @param file MultipartFile containing the video
   * @throws NotFoundException if the course does not exist
   */
  @Transactional
  public void uploadVideo(Long courseId, String title, MultipartFile file) {

    final var course =
        this.courseRepository
            .findById(courseId)
            .orElseThrow(() -> new NotFoundException(StaticMessage.COURSE_NOT_FOUND));

    Map<String, String> result = videoServiceCloudinary.uploadVideoCustom(file);

    Video video = new Video();
    video.setTitle(title);
    video.setPublicId(result.get("publicId"));
    video.setDuration(Long.valueOf(result.get("duration")));
    video.setCourse(course);

    super.save(video);

    updateCourseTotalDuration(courseId);
  }

  /**
   * Retrieves signed URLs for all videos in a course for a student.
   *
   * @param courseId ID of the course
   * @return List of signed video URLs
   * @throws AccessDeniedException if the user is not enrolled in the course
   */
  @Transactional(readOnly = true)
  public List<String> getVideoStudentInrollment(Long courseId) {
    User user = UserContext.getUser();

    boolean hasAccess = this.enrollmentService.canAccess(user.getId(), courseId);
    if (!hasAccess) {
      throw new AccessDeniedException("Access denied");
    }

    return super.baseRepository.findAllByCourseId(courseId, super.toPageable(1, 100)).stream()
        .map(video -> videoServiceCloudinary.generateSignedUrl(video.getPublicId()))
        .toList();
  }

  /**
   * Retrieves a watchable video URL by its publicId.
   *
   * @param publicId Public ID of the video
   * @return Watch URL for the video
   */
  @Transactional(readOnly = true)
  public String watchVideo(String publicId) {
    return videoServiceCloudinary.watchVideo(publicId);
  }

  /**
   * Deletes a single video by its publicId.
   *
   * @param publicId Public ID of the video to delete
   */
  @Transactional
  public void deleteVideo(String publicId, Long videoId) {
    final var video = super.findById(videoId);

    final var courseId = video.getCourse().getId();

    super.deleteById(videoId);
    videoServiceCloudinary.deleteVideo(publicId);

    updateCourseTotalDuration(courseId);
  }

  /**
   * Updates a video file by replacing it with a new file.
   *
   * @return Public ID of the updated video
   */
  @Transactional
  public String updateVideo(UpdateVideoRequest request, Long videoId, Long courseId) {
    var video = super.findById(videoId);
    Utils.setValueSafe(request.getTitle(), video::setTitle);

    if (request.getFile() != null) {
      var newPublicId = videoServiceCloudinary.updateVideo(video.getPublicId(), request.getFile());
      video.setPublicId(newPublicId);
      video.setDuration(request.getDuration());
    }
    super.update(video);
    if (courseId != null) {
      updateCourseTotalDuration(courseId);
    }
    return "Update video successfully";
  }

  /**
   * Deletes all videos in a specific course.
   *
   * @param courseId ID of the course
   */
  @Transactional
  public void deleteAllVideoInCourse(Long courseId) {

    var videos = this.baseRepository.findAllByCourseId(courseId, super.toPageable(1, 100));
    var publicIds = videos.stream().map(Video::getPublicId).toList();

    videoServiceCloudinary.deleteVideos(publicIds);
    this.baseRepository.deleteAllInBatch(videos);
    updateCourseTotalDuration(courseId);
  }

  private void updateCourseTotalDuration(Long courseId) {
    // Sum durations of all videos for this course
    var totalDuration = super.baseRepository.sumDurationByCourseId(courseId);

    Course course = courseRepository.findById(courseId).orElseThrow();
    course.setDuration(totalDuration != null ? totalDuration : 0.0);
    courseRepository.save(course);
  }
}

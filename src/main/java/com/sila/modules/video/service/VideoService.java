package com.sila.modules.video.service;

import com.sila.config.context.UserContext;
import com.sila.config.exception.AccessDeniedException;
import com.sila.config.exception.NotFoundException;
import com.sila.modules.course.repository.CourseRepository;
import com.sila.modules.enrolment.service.EnrollmentService;
import com.sila.modules.profile.model.User;
import com.sila.modules.video.dto.VideoListResponse;
import com.sila.modules.video.model.Video;
import com.sila.modules.video.repository.VideoRepository;
import com.sila.modules.video.spec.VideoSpec;
import com.sila.share.constant.StaticMessage;
import com.sila.share.core.crud.AbstractCrudCommon;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.enums.ROLE;
import java.util.List;
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
  private final CloudinaryService cloudinaryService;

  protected VideoService(
      VideoRepository baseRepository,
      ModelMapper mapper,
      EnrollmentService enrollmentService,
      CourseRepository courseRepository,
      CloudinaryService cloudinaryService) {
    super(baseRepository, mapper);
    this.enrollmentService = enrollmentService;
    this.courseRepository = courseRepository;
    this.cloudinaryService = cloudinaryService;
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
  public EntityResponseHandler<VideoListResponse> getVideosInCourse(
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
    return new EntityResponseHandler<>(videos);
  }

  /**
   * Retrieves all videos globally with pagination and search filters.
   *
   * @param paginationRequest Pagination parameters and search term
   * @return Paginated list of VideoListResponse
   */
  @Transactional(readOnly = true)
  public EntityResponseHandler<VideoListResponse> getAllVideos(
      PaginationRequest paginationRequest) {
    var pageable =
        super.toPageable(
            paginationRequest.getPage(),
            paginationRequest.getLimit(),
            paginationRequest.getSortBy(),
            String.valueOf(paginationRequest.getSortOrder()));
    var spec = VideoSpec.search(paginationRequest.getSearch());
    final var videoPage = super.findAll(spec, pageable);
    final var videos = videoPage.map(vd -> mapper.map(vd, VideoListResponse.class));
    return new EntityResponseHandler<>(videos);
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

    String publicId = cloudinaryService.uploadVideo(file);

    Video video = new Video();
    video.setTitle(title);
    video.setPublicId(publicId);
    video.setCourse(course);

    super.save(video);
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
        .map(video -> cloudinaryService.generateSignedUrl(video.getPublicId()))
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
    return cloudinaryService.watchVideo(publicId);
  }

  /**
   * Deletes a single video by its publicId.
   *
   * @param publicId Public ID of the video to delete
   */
  @Transactional
  public void deleteVideo(String publicId) {
    cloudinaryService.deleteVideo(publicId);
  }

  /**
   * Updates a video file by replacing it with a new file.
   *
   * @param oldPublicId Public ID of the video to update
   * @param file New MultipartFile video
   * @return Public ID of the updated video
   */
  @Transactional
  public String updateVideo(String oldPublicId, MultipartFile file) {
    return cloudinaryService.updateVideo(oldPublicId, file);
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

    cloudinaryService.deleteVideos(publicIds);
    this.baseRepository.deleteAllInBatch(videos);
  }
}

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
import com.sila.share.core.crud.AbstractCrudCommon;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.enums.ROLE;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

  @Transactional(readOnly = true)
  public EntityResponseHandler<VideoListResponse> getVideosInCourse(
      Long courseId, PaginationRequest paginationRequest) {

    if (!this.enrollmentService.canAccess(UserContext.getUserId(), courseId)
        && UserContext.getUserRole() != ROLE.ADMIN) {
      throw new AccessDeniedException("Access denied");
    }

    var pageable = super.toPageable(paginationRequest.getPage(), paginationRequest.getLimit());
    var spec = VideoSpec.search(paginationRequest.getSearch()).and(VideoSpec.byCourseId(courseId));
    final var videoPage = this.baseRepository.findAll(spec, pageable);
    final var videos = videoPage.map(vd -> mapper.map(vd, VideoListResponse.class));
    return new EntityResponseHandler<>(videos);
  }

  @Transactional(readOnly = true)
  public EntityResponseHandler<VideoListResponse> getAllVideos(
      PaginationRequest paginationRequest) {
    var pageable = super.toPageable(paginationRequest.getPage(), paginationRequest.getLimit());
    var spec = VideoSpec.search(paginationRequest.getSearch());
    final var videoPage = super.findAll(spec, pageable);
    final var videos = videoPage.map(vd -> mapper.map(vd, VideoListResponse.class));
    return new EntityResponseHandler<>(videos);
  }

  @Transactional
  public void uploadVideo(Long courseId, String title, MultipartFile file) {

    final var course =
        this.courseRepository
            .findById(courseId)
            .orElseThrow(() -> new NotFoundException("Course not found"));

    //    upload vdo to cloudinary
    String publicId = cloudinaryService.uploadVideo(file);

    Video video = new Video();
    video.setTitle(title);
    video.setPublicId(publicId);
    video.setCourse(course);

    super.save(video);
  }

  @Transactional(readOnly = true)
  public List<String> getVideoStudentInrollment(Long courseId) {
    User user = UserContext.getUser();

    // 🔒 Check enrollment first
    boolean hasAccess = this.enrollmentService.canAccess(user.getId(), courseId);
    if (!hasAccess) {
      throw new AccessDeniedException("Access denied");
    }

    return super.baseRepository.findAllByCourseId(courseId, super.toPageable(1, 100)).stream()
        .map(video -> cloudinaryService.generateSignedUrl(video.getPublicId()))
        .toList();
  }

  @Transactional(readOnly = true)
  public String watchVideo(String publicId) {
    return cloudinaryService.watchVideo(publicId);
  }

  @Transactional
  public void deleteVideo(String publicId) {
    cloudinaryService.deleteVideo(publicId);
  }

  @Transactional
  public String updateVideo(String oldPublicId, MultipartFile file) {
    return cloudinaryService.updateVideo(oldPublicId, file);
  }

  /** Delete Video In Course */
  @Transactional
  public void deleteAllVideoInCourse(Long courseId) {

    var videos = this.baseRepository.findAllByCourseId(courseId, super.toPageable(1, 100));

    var publicIds = videos.stream().map(Video::getPublicId).toList();

    cloudinaryService.deleteVideos(publicIds);

    this.baseRepository.deleteAllInBatch(videos);
  }
}

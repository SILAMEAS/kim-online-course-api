package com.sila.modules.video.service;

import com.sila.config.context.UserContext;
import com.sila.config.exception.AccessDeniedException;
import com.sila.modules.course.repository.CourseRepository;
import com.sila.modules.enrolment.service.EnrollmentService;
import com.sila.modules.profile.model.User;
import com.sila.modules.video.dto.VideoListResponse;
import com.sila.modules.video.model.Video;
import com.sila.modules.video.repository.VideoRepository;
import com.sila.modules.video.spec.VideoSpec;
import com.sila.share.core.crud.AbstractCrudCommon;
import com.sila.share.enums.ROLE;
import com.sila.share.pagination.EntityResponseHandler;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
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

  public EntityResponseHandler<VideoListResponse> getVideos(Long courseId) {

    if (!this.enrollmentService.canAccess(UserContext.getUserId(), courseId)
        && UserContext.getUserRole() != ROLE.ADMIN) {
      throw new AccessDeniedException("Access denied");
    }

    var pageable = super.toPageable(1, 10);
    var spec = VideoSpec.search("");
    final var videoPage = super.findAll(spec, pageable);
    final var videos = videoPage.map(vd -> mapper.map(vd, VideoListResponse.class));
    return new EntityResponseHandler<>(videos);
  }

  public void uploadVideo(Long courseId, String title, MultipartFile file) {

    final var course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));

    String publicId = cloudinaryService.uploadVideo(file);

    Video video = new Video();
    video.setTitle(title);
    video.setPublicId(publicId);
    video.setCourse(course);

    super.save(video);
  }

  public List<String> getVideosForStudent(Long courseId) {
    User user = UserContext.getUser();

    // 🔒 Check enrollment first
    boolean hasAccess = checkEnrollment(user.getId(), courseId);
    if (!hasAccess) {
      throw new AccessDeniedException("Access denied");
    }

    return super.baseRepository.findByCourseId(courseId).stream()
        .map(video -> cloudinaryService.generateSignedUrl(video.getPublicId()))
        .toList();
  }

  private boolean checkEnrollment(Long userId, Long courseId) {
    // implement your enrollment check here
    return true; // replace with real logic
  }
}

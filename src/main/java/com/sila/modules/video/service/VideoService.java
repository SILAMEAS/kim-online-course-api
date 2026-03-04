package com.sila.modules.video.service;

import com.sila.config.context.UserContext;
import com.sila.config.exception.AccessDeniedException;
import com.sila.modules.enrolment.service.EnrollmentService;
import com.sila.modules.profile.model.User;
import com.sila.modules.video.model.Video;
import com.sila.modules.video.repository.VideoRepository;
import com.sila.share.core.crud.AbstractCrudCommon;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class VideoService extends AbstractCrudCommon<Video, Long, VideoRepository> {
  final EnrollmentService enrollmentService;

  protected VideoService(
      VideoRepository baseRepository, ModelMapper mapper, EnrollmentService enrollmentService) {
    super(baseRepository, mapper);
    this.enrollmentService = enrollmentService;
  }

  public List<?> getVideos(Long courseId) {
    User user = UserContext.getUser();

    if (!this.enrollmentService.canAccess(user.getId(), courseId)) {
      throw new AccessDeniedException("Access denied");
    }

    return null;
  }
}

package com.sila.modules.video.repository;

import com.sila.modules.video.model.Video;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VideoRepository
    extends JpaRepository<Video, Long>, JpaSpecificationExecutor<Video> {
  List<Video> findByCourseId(Long courseId);
}

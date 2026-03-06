package com.sila.modules.video.repository;

import com.sila.modules.video.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VideoRepository
    extends JpaRepository<Video, Long>, JpaSpecificationExecutor<Video> {
    Page<Video> findAllByCourseId(Long courseId,
                                  Pageable pageable);
}

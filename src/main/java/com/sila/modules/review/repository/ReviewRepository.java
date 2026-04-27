package com.sila.modules.review.repository;

import com.sila.modules.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReviewRepository
        extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review>, ReviewRepositoryCustom {

  Long countAllByCourseId(Long courseId);
}

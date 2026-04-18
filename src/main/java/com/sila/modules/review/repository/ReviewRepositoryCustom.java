package com.sila.modules.review.repository;

import com.sila.modules.review.dto.RatingStats;
import com.sila.modules.review.dto.ReviewSummary;
import com.sila.modules.review.model.Review;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ReviewRepositoryCustom {
    RatingStats getOverallStats(Specification<Review> spec);
    List<ReviewSummary> getReviewBreakdown(Specification<Review> spec);
}
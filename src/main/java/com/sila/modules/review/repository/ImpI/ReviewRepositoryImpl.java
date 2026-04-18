package com.sila.modules.review.repository.ImpI;

import com.sila.modules.review.dto.RatingStats;
import com.sila.modules.review.dto.ReviewSummary;
import com.sila.modules.review.model.Review;
import com.sila.modules.review.repository.ReviewRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public RatingStats getOverallStats(Specification<Review> spec) {

        var cb = em.getCriteriaBuilder();
        var query = cb.createQuery(Object[].class);
        var root = query.from(Review.class);

        query.distinct(false); // 🔥 critical

        var predicate = getPredicate(spec, root, query, cb);

        query.multiselect(
                cb.avg(root.get("rating")),
                cb.count(root.get("id")) // 🔥 count ALL rows
        ).where(predicate);

        var result = em.createQuery(query).getSingleResult();

        return new RatingStats(
                result[0] != null ? (Double) result[0] : 0.0,
                result[1] != null ? (Long) result[1] : 0L
        );
    }

    @Override
    public List<ReviewSummary> getReviewBreakdown(Specification<Review> spec) {

        var cb = em.getCriteriaBuilder();
        var query = cb.createQuery(ReviewSummary.class);
        var root = query.from(Review.class);

        query.distinct(false); // 🔥 critical

        var predicate = getPredicate(spec, root, query, cb);

        query.select(cb.construct(
                        ReviewSummary.class,
                        root.get("rating"),
                        cb.count(root.get("id"))
                ))
                .where(predicate)
                .groupBy(root.get("rating")) // group by rating (5,4,3...)
                .orderBy(cb.desc(root.get("rating")));

        return em.createQuery(query).getResultList();
    }

    private Predicate getPredicate(Specification<Review> spec,
                                   Root<Review> root,
                                   CriteriaQuery<?> query,
                                   CriteriaBuilder cb) {

        return (spec != null)
                ? spec.toPredicate(root, query, cb)
                : cb.conjunction();
    }
}
package com.sila.modules.wishlist.repository;

import com.sila.modules.profile.model.User;
import com.sila.modules.wishlist.model.Wishlist;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WishlistRepository
    extends JpaRepository<Wishlist, Long>, JpaSpecificationExecutor<Wishlist> {

  Page<Wishlist> findByUserId(Long userId, Pageable pageable);

  Optional<Wishlist> findByUserIdAndCourseId(Long userId, Long courseId);

  void deleteByUserIdAndCourseId(Long userId, Long courseId);

  boolean existsByUserIdAndCourseId(Long userId, Long courseId);

  boolean existsByUser_IdAndCourse_Id(Long userId, Long courseId);
}

package com.sila.modules.wishlist.service;

import com.sila.config.context.UserContext;
import com.sila.config.exception.BadRequestException;
import com.sila.modules.course.mapping.CourseMapping;
import com.sila.modules.course.service.CourseService;
import com.sila.modules.profile.dto.res.UserResponse;
import com.sila.modules.wishlist.dto.WishlistResponse;
import com.sila.modules.wishlist.model.Wishlist;
import com.sila.modules.wishlist.repository.WishlistRepository;
import com.sila.modules.wishlist.spec.WishlistSpec;
import com.sila.share.core.crud.AbstractCrudCommon;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.core.pagination.ResponsePaginationHandler;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishlistService extends AbstractCrudCommon<Wishlist, Long, WishlistRepository> {
  private final CourseService courseService;
  private final CourseMapping courseMapping;

  protected WishlistService(
      WishlistRepository baseRepository,
      ModelMapper mapper,
      CourseService courseService,
      CourseMapping courseMapping) {
    super(baseRepository, mapper);
    this.courseService = courseService;
    this.courseMapping = courseMapping;
  }

  @Transactional
  public void addToWishlist(Long courseId) {
    final var course = this.courseService.findByIdService(courseId);

    if (super.baseRepository.existsByUserIdAndCourseId(UserContext.getUserId(), course.getId())) {
      throw new BadRequestException("Already in wishlist");
    }

    Wishlist wishlist = Wishlist.builder().user(UserContext.getUser()).course(course).build();

    super.save(wishlist);
  }

  @Transactional
  public void removeFromWishlist(Long courseId) {
    super.baseRepository.deleteByUserIdAndCourseId(UserContext.getUserId(), courseId);
  }

  @Transactional(readOnly = true)
  public ResponsePaginationHandler<WishlistResponse> getUserWishlist(
      PaginationRequest request, Long userId) {

    final var pageable = super.toPageable(request);

    var spec = WishlistSpec.byUserId(userId).and(WishlistSpec.fetchRelationsDetailOptimized());

    Page<Wishlist> page = baseRepository.findAll(spec, pageable);

    return new ResponsePaginationHandler<>(
        page.map(
            w ->
                WishlistResponse.builder()
                    .id(w.getId())
                    .course(courseMapping.mapToCourseResponse(w.getCourse()))
                    .user(this.mapper.map(w.getUser(), UserResponse.class))
                    .build()));
  }

  @Transactional
  public boolean exists(Long userId, Long courseId) {
    return super.baseRepository.existsByUserIdAndCourseId(userId, courseId);
  }
}

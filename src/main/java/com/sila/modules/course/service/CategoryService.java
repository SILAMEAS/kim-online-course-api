package com.sila.modules.course.service;

import com.sila.config.exception.BadRequestException;
import com.sila.modules.course.dto.CreateCategoryRequest;
import com.sila.modules.course.model.Category;
import com.sila.modules.course.repository.CategoryRepository;
import com.sila.modules.course.repository.CourseRepository;
import com.sila.modules.course.spec.CategorySpec;
import com.sila.share.Utils;
import com.sila.share.core.crud.AbstractCrudCommon;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.core.pagination.ResponsePaginationHandler;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class CategoryService extends AbstractCrudCommon<Category, Long, CategoryRepository> {
  private final CourseRepository courseRepository;

  protected CategoryService(
      CategoryRepository baseRepository, ModelMapper mapper, CourseRepository courseRepository) {
    super(baseRepository, mapper);
    this.courseRepository = courseRepository;
  }

  public ResponsePaginationHandler<Category> list(PaginationRequest request) {
    final var pageable =
        super.toPageable(
            request.getPage(),
            request.getLimit(),
            request.getSortBy(),
            String.valueOf(request.getSortOrder()));
    final var spec = CategorySpec.search(request.getSearch());
    Page<Category> courses = super.findAll(spec, pageable);
    return new ResponsePaginationHandler<>(courses);
  }

  public Category create(CreateCategoryRequest request) {
    var newCategory = new Category();
    Utils.setValueSafe(request.getName(), newCategory::setName);

    return super.save(newCategory);
  }

  public Category findOne(Long id) {
    return super.findById(id);
  }

  public void deleteCategory(Long categoryId) {
    super.findById(categoryId);
    boolean isUsed = courseRepository.existsByCategoryId(categoryId);
    if (isUsed) {
      // Throw a custom exception or return a message to the user
      throw new BadRequestException(
          "Cannot delete category: It is currently assigned to active courses.");
    }
    super.deleteById(categoryId);
  }

  public void modify(Long categoryId,CreateCategoryRequest createCategoryRequest) {
    var category = super.findById(categoryId);
    Utils.setValueSafe(createCategoryRequest.getName(),category::setName);
    super.update(category);
  }
}

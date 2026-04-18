package com.sila.modules.category.controller;

import com.sila.modules.category.dto.CreateCategoryRequest;
import com.sila.modules.category.model.Category;
import com.sila.modules.category.service.CategoryService;
import com.sila.modules.category.dto.CategoryPageResponse;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.core.pagination.ResponsePaginationHandler;
import com.sila.share.dto.GeneralResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @GetMapping
  @Operation(
      summary = "list categories",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "list categories response successfully",
            content = @Content(schema = @Schema(implementation = CategoryPageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid course data"),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<ResponsePaginationHandler<Category>> listCategories(
      @ParameterObject @Validated PaginationRequest request) {
    return ResponseEntity.ok(categoryService.list(request));
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(
      summary = "Create categories",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "create categories response successfully",
            content = @Content(schema = @Schema(implementation = Category.class))),
        @ApiResponse(responseCode = "400", description = "Invalid course data"),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<Category> createCategory(
      @Valid @ModelAttribute CreateCategoryRequest request) {
    return ResponseEntity.ok(categoryService.create(request));
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "delete categories",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "delete categories response successfully",
            content = @Content(schema = @Schema(implementation = GeneralResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid course data"),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<GeneralResponse> deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.ok(
        GeneralResponse.builder().message("Delete category successes").status(200).build());
  }

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(
      summary = "update categories",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "update categories response successfully",
            content = @Content(schema = @Schema(implementation = GeneralResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid course data"),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<GeneralResponse> updateCategory(
      @PathVariable Long id, @Valid @ModelAttribute CreateCategoryRequest request) {
    categoryService.modify(id, request);
    return ResponseEntity.ok(
        GeneralResponse.builder().message("Update category successes").status(200).build());
  }
}

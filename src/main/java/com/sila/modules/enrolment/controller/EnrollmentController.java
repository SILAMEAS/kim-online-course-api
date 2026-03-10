package com.sila.modules.enrolment.controller;

import com.sila.modules.enrolment.dto.EnrollmentResponse;
import com.sila.modules.enrolment.service.EnrollmentService;
import com.sila.share.annotation.PreAuthorization;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.enums.ROLE;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/enrollments")
public class EnrollmentController {
  private final EnrollmentService paymentService;

  public EnrollmentController(EnrollmentService paymentService1) {
    this.paymentService = paymentService1;
  }

  @GetMapping
  @PreAuthorization({ROLE.ADMIN, ROLE.STUDENT})
  ResponseEntity<EntityResponseHandler<EnrollmentResponse>> getAll(
      @ModelAttribute PaginationRequest paginationRequest) {
    return ResponseEntity.ok(this.paymentService.listAllEnrollment(null, paginationRequest));
  }

  @GetMapping("courses/{id}")
  @PreAuthorization({ROLE.ADMIN, ROLE.STUDENT})
  ResponseEntity<EntityResponseHandler<EnrollmentResponse>> getAllByCourse(
      @PathVariable Long id, @ModelAttribute PaginationRequest paginationRequest) {
    return ResponseEntity.ok(this.paymentService.listAllEnrollment(id, paginationRequest));
  }

  @DeleteMapping("courses/{id}")
  @PreAuthorization({ROLE.ADMIN})
  ResponseEntity<String> deleteAllByCourse(@PathVariable Long id) {
    return ResponseEntity.ok(this.paymentService.bulkDeleteByCourseId(id));
  }
}

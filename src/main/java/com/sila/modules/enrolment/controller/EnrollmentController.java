package com.sila.modules.enrolment.controller;

import com.sila.modules.enrolment.dto.EnrollmentResponse;
import com.sila.modules.enrolment.service.EnrollmentService;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.PaginationRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/enrollments")
public class EnrollmentController {
  private final EnrollmentService paymentService;

  public EnrollmentController(EnrollmentService paymentService1) {
    this.paymentService = paymentService1;
  }

  @GetMapping
  EntityResponseHandler<EnrollmentResponse> getAll(
      @ModelAttribute PaginationRequest paginationRequest) {
    return this.paymentService.listAllEnrollment(paginationRequest);
  }
}

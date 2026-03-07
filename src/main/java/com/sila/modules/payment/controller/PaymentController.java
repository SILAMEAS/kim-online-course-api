package com.sila.modules.payment.controller;

import com.sila.modules.enrolment.dto.EnrollmentResponse;
import com.sila.modules.payment.dto.ListPaymentResponse;
import com.sila.modules.payment.dto.PaymentResponse;
import com.sila.modules.payment.service.PaymentService;
import com.sila.share.annotation.PreAuthorization;
import com.sila.share.core.pagination.EntityResponseHandler;
import com.sila.share.core.pagination.PaginationRequest;
import com.sila.share.enums.ROLE;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @Operation(description = "Operation list payment")
  @GetMapping
  @PreAuthorization({ROLE.ADMIN, ROLE.STUDENT})
  public ResponseEntity<EntityResponseHandler<ListPaymentResponse>> getAllPayments(
      @ModelAttribute @Validated PaginationRequest request) {
    return ResponseEntity.ok(paymentService.listPayments(request));
  }

  @Operation(description = "Operation  submit their payment")
  @PostMapping("/{courseId}")
  @PreAuthorization(ROLE.STUDENT)
  public ResponseEntity<PaymentResponse> submitPayment(@PathVariable Long courseId) {
    return ResponseEntity.ok(paymentService.createPayments(courseId));
  }

  @Operation(
      description =
          "Operation User ADMIN approve payment to enroll student that payment into course")
  @PostMapping("/{id}/approve")
  @PreAuthorization(ROLE.ADMIN)
  public ResponseEntity<EnrollmentResponse> approve(@PathVariable Long id) {

    return ResponseEntity.ok(this.paymentService.approvePayment(id));
  }
}

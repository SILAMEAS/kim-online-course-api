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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@Tag(
    name = "Payment Management",
    description = "Manage course payments and approve student enrollments")
public class PaymentController {

  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  /** Get all payments */
  @GetMapping
  @PreAuthorization({ROLE.ADMIN, ROLE.STUDENT})
  @Operation(
      summary = "Get all payments",
      description =
          "Retrieve a paginated list of payments. "
              + "ADMIN can view all payments, STUDENT can view their own payments.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Payments retrieved successfully",
            content = @Content(schema = @Schema(implementation = ListPaymentResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied")
      })
  public ResponseEntity<EntityResponseHandler<ListPaymentResponse>> getAllPayments(
      @ParameterObject @Validated PaginationRequest request) {

    return ResponseEntity.ok(paymentService.listPayments(request));
  }

  /** Submit payment */
  @PostMapping("/{courseId}")
  @PreAuthorization(ROLE.STUDENT)
  @Operation(
      summary = "Submit payment for a course",
      description =
          "Student submits payment for a specific course. "
              + "The payment will be pending until approved by ADMIN.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Payment submitted successfully",
            content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "403", description = "Only STUDENT can submit payment")
      })
  public ResponseEntity<PaymentResponse> submitPayment(
      @Parameter(
              description = "ID of the course the student wants to enroll in",
              example = "1",
              required = true)
          @PathVariable
          Long courseId) {

    return ResponseEntity.ok(paymentService.createPayments(courseId));
  }

  /** Approve payment */
  @PostMapping("/{paymentId}/approve")
  @PreAuthorization(ROLE.ADMIN)
  @Operation(
      summary = "Approve a payment",
      description =
          "ADMIN approves a student's payment. "
              + "After approval, the student will be automatically enrolled in the course.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Payment approved and enrollment created",
            content = @Content(schema = @Schema(implementation = EnrollmentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "403", description = "Only ADMIN can approve payment")
      })
  public ResponseEntity<EnrollmentResponse> approve(
      @Parameter(description = "ID of the payment to approve", example = "10", required = true)
          @PathVariable
          Long paymentId) {

    return ResponseEntity.ok(paymentService.approvePayment(paymentId));
  }
}

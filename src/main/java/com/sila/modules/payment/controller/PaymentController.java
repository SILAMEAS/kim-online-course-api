package com.sila.modules.payment.controller;

import com.sila.modules.payment.dto.PaymentRequest;
import com.sila.share.annotation.PreAuthorization;
import com.sila.share.enums.ROLE;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

  @Operation(description = "Operation student try to submit their payment")
  @PreAuthorization({ROLE.STUDENT})
  public ResponseEntity<?> submitPayment(@RequestBody PaymentRequest req) {

    //    Payment payment = new Payment();
    //    payment.setUser(new User());
    //    payment.setCourse(courseRepo.findById(req.getCourseId()).orElseThrow());
    //    payment.setAmount(req.getAmount());
    //    payment.setStatus("WAITING_APPROVAL");
    //    payment.setProofImage(req.getProofImage());
    //
    //    paymentRepo.save(payment);

    return ResponseEntity.ok("Payment submitted");
  }

  @Operation(
      description =
          "Operation User ADMIN approve payment to enroll student that payment into course")
  @PostMapping("/{id}/approve")
  @PreAuthorization(ROLE.ADMIN)
  public ResponseEntity<?> approve(@PathVariable Long id) {

    //    Payment payment = paymentRepo.findById(id).orElseThrow();
    //
    //    payment.setStatus("APPROVED");
    //    payment.setApprovedAt(LocalDateTime.now());
    //    paymentRepo.save(payment);
    //
    //    Enrollment enroll = new Enrollment();
    //    enroll.setUser(payment.getUser());
    //    enroll.setCourse(payment.getCourse());
    //    enroll.setPayment(payment);
    //    enroll.setStatus("ACTIVE");
    //
    //    enrollmentRepo.save(enroll);

    return ResponseEntity.ok("Approved");
  }
}

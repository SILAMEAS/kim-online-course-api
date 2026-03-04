package com.sila.modules.profile.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admins")
public class AdminController {
  @PostMapping("/payments/{id}/approve")
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

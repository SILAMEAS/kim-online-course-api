package com.sila.modules.payment.controller;

import com.sila.modules.payment.dto.PaymentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
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
}

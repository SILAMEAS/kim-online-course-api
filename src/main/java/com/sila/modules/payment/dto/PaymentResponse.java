package com.sila.modules.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponse {

  private Long id;
  private Double amount;
  private String status;

  private Long courseId;
  private String courseTitle;

  private Long userId;
}

package com.sila.modules.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListPaymentResponse {

  private Long id;
  private Double amount;
  private String status;
}

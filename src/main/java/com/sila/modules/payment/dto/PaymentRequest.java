package com.sila.modules.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

  @NotBlank(message = "course id is required")
  private Long courseId;

  @NotBlank(message = "amount id is required")
  private Double amount;
}

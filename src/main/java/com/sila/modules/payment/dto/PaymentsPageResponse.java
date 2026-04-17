package com.sila.modules.payment.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentsPageResponse {
  private List<ListPaymentResponse> contents;
  private int page;
  private int limit;
  private long total;
  private int totalPage;
  private boolean hasNext;
}

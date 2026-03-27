package com.sila.share.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneralResponse {

  /** Human-readable message describing the response outcome. */
  private String message;

  /** Timestamp indicating when the response was generated. */
  private Number status;
}

package com.sila.modules.enrolment.dto;

import com.sila.modules.enrolment.Enum.EnrollmentStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EnrollmentResponse {
  private Long id;
  private EnrollmentStatus status;
}

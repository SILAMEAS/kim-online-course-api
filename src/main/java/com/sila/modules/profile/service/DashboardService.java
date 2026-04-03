package com.sila.modules.profile.service;

import com.sila.modules.course.repository.CourseRepository;
import com.sila.modules.enrolment.repository.EnrollmentRepository;
import com.sila.modules.image.repository.ImageRepository;
import com.sila.modules.payment.Enum.PaymentStatus;
import com.sila.modules.payment.spec.PaymentSpec;
import com.sila.modules.payment.spec.PaymentSumUtil;
import com.sila.modules.profile.dto.res.DashboardResponse;
import com.sila.modules.profile.repository.UserRepository;
import com.sila.modules.video.repository.VideoRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.util.Locale;

@Service
public class DashboardService {
  private final UserRepository userRepository;
  private final VideoRepository videoRepository;
  private final CourseRepository courseRepository;
  private final ImageRepository imageRepository;
  private final EnrollmentRepository enrollmentRepository;
  private final PaymentSumUtil paymentSumUtil;

  public DashboardService(
          UserRepository userRepository,
          VideoRepository videoRepository,
          CourseRepository courseRepository,
          ImageRepository imageRepository,
          EnrollmentRepository enrollmentRepository, PaymentSumUtil paymentSumUtil) {
    this.userRepository = userRepository;
    this.videoRepository = videoRepository;
    this.courseRepository = courseRepository;
    this.imageRepository = imageRepository;
    this.enrollmentRepository = enrollmentRepository;
      this.paymentSumUtil = paymentSumUtil;
  }

  @Transactional
  public DashboardResponse getDashboard() {
    // Build dynamic spec: DONE payments + ownership filter (or other filters)
    var spec = Specification.where(PaymentSpec.byOwnership())
            .and((root, query, cb) -> cb.equal(root.get("status"), PaymentStatus.DONE));

    Double totalRevenue = paymentSumUtil.sumAmount(spec);

    return DashboardResponse.builder()
        .totalUsers(userRepository.count())
        .totalVideos(videoRepository.count())
        .totalCourses(courseRepository.count())
        .totalImages(imageRepository.count())
        .totalRevenues(NumberFormat.getCurrencyInstance(Locale.US).format(totalRevenue))
        .totalEnrollments(enrollmentRepository.count())
        .build();
  }
}

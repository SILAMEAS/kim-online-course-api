package com.sila.modules.profile.service;

import com.sila.modules.category.repository.CategoryRepository;
import com.sila.modules.course.repository.CourseRepository;
import com.sila.modules.enrolment.repository.EnrollmentRepository;
import com.sila.modules.image.repository.ImageRepository;
import com.sila.modules.payment.Enum.PaymentStatus;
import com.sila.modules.payment.repository.PaymentRepository;
import com.sila.modules.payment.spec.PaymentSpec;
import com.sila.modules.profile.dto.res.DashboardResponse;
import com.sila.modules.profile.dto.res.DashboardUserResponse;
import com.sila.modules.profile.repository.UserRepository;
import com.sila.modules.video.repository.VideoRepository;
import com.sila.share.enums.ROLE;
import java.text.NumberFormat;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {
  private final UserRepository userRepository;
  private final VideoRepository videoRepository;
  private final CourseRepository courseRepository;
  private final ImageRepository imageRepository;
  private final EnrollmentRepository enrollmentRepository;
  private final PaymentRepository paymentRepository;
  private final CategoryRepository categoryRepository;

  public DashboardService(
      UserRepository userRepository,
      VideoRepository videoRepository,
      CourseRepository courseRepository,
      ImageRepository imageRepository,
      EnrollmentRepository enrollmentRepository,
      PaymentRepository paymentRepository, CategoryRepository categoryRepository) {
    this.userRepository = userRepository;
    this.videoRepository = videoRepository;
    this.courseRepository = courseRepository;
    this.imageRepository = imageRepository;
    this.enrollmentRepository = enrollmentRepository;
    this.paymentRepository = paymentRepository;
    this.categoryRepository = categoryRepository;
  }

  @Transactional
  public DashboardResponse getDashboard() {
    // Build dynamic spec: DONE payments + ownership filter (or other filters)
    var spec =
        Specification.where(PaymentSpec.byOwnership())
            .and((root, query, cb) -> cb.equal(root.get("status"), PaymentStatus.DONE));

    Double totalRevenue = this.paymentRepository.sumAmount(spec);

    return DashboardResponse.builder()
        .totalUsers(this.userRepository.count())
        .totalStudents(this.userRepository.countAllByRole(ROLE.STUDENT))
        .totalTeachers(this.userRepository.countAllByRole(ROLE.INSTRUCTOR))
        .totalVideos(this.videoRepository.count())
        .totalCourses(this.courseRepository.count())
        .totalImages(this.imageRepository.count())
        .totalRevenues(NumberFormat.getCurrencyInstance(Locale.US).format(totalRevenue))
        .totalEnrollments(this.enrollmentRepository.count())
        .totalCategories(this.categoryRepository.count())
        .build();
  }

  @Transactional
  public DashboardUserResponse getDashboardUser() {
    return DashboardUserResponse.builder().enrolled(12L).certificates(12L).learningStreak(123L).timeComplete(123L).build();
  }
}

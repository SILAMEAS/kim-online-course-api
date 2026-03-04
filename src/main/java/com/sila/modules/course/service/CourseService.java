package com.sila.modules.course.service;

import com.sila.modules.course.dto.CourseResponse;
import com.sila.modules.course.dto.CreateCourseRequest;
import com.sila.modules.course.model.Course;
import com.sila.modules.course.repository.CourseRepository;
import com.sila.modules.course.spec.CourseSpec;
import com.sila.modules.profile.dto.res.UserResponse;
import com.sila.modules.profile.service.UserService;
import com.sila.share.core.crud.AbstractCrudCommon;
import com.sila.share.core.pagiation.EntityResponseHandler;
import com.sila.share.core.pagiation.PaginationRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseService extends AbstractCrudCommon<Course, Long, CourseRepository> {
    final UserService userService;

    protected CourseService(CourseRepository baseRepository, ModelMapper mapper, UserService userService) {
        super(baseRepository, mapper);
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public EntityResponseHandler<CourseResponse> listCourse(PaginationRequest request) {
        //create pageable from request
        final var pageable =
                super.toPageable(
                        request.getPage(),
                        request.getLimit(),
                        request.getSortBy(),
                        String.valueOf(request.getSortOrder()));
        //spec for search, find and filter
        final var spec = CourseSpec.search(request.getSearch());
        //convert from entity to dto
        final var courses = super.findAll(spec, pageable).map(c -> this.mapper.map(c, CourseResponse.class));
        return new EntityResponseHandler<>(courses);
    }

    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request) {

        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setPrice(request.getPrice());
        var instructor = userService.getById(request.getInstructorId());
        course.setInstructor(instructor);

        super.save(course);

        return null;
    }

}

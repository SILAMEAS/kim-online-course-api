package com.sila.modules.course.service;

import com.sila.modules.course.dto.CourseResponse;
import com.sila.modules.course.model.Course;
import com.sila.modules.course.repository.CourseRepository;
import com.sila.modules.course.spec.CourseSpec;
import com.sila.share.core.AbstractCrudCommon;
import com.sila.share.core.EntityResponseHandler;
import com.sila.share.core.PaginationRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class CourseService extends AbstractCrudCommon<Course, Long, CourseRepository> {
    protected CourseService(CourseRepository baseRepository, ModelMapper mapper) {
        super(baseRepository, mapper);
    }

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
}

package com.sila.modules.course.repository;

import com.sila.modules.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository
        extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
}
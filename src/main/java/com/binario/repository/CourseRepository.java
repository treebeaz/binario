package com.binario.repository;

import com.binario.entity.Course;
import com.binario.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByCreatedBy(User teacher);
}

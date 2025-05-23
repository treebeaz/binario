package com.binario.repository;

import com.binario.entity.Course;
import com.binario.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByName(String name);
    boolean existsByName(String name);
    List<Course> findByCreatedBy(User teacher);

}

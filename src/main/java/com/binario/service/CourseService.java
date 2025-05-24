package com.binario.service;

import com.binario.entity.Course;
import com.binario.entity.User;
import com.binario.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourse() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }

    public List<Course> getCourseByTeacher(User teacher) {
        return courseRepository.findByCreatedBy(teacher);
    }

    @Transactional
    public void saveCourse(Course newCourse) {
        if(newCourse.getId() == null) {
            Course savedCourse = new Course();
            savedCourse.setName(newCourse.getName());
            savedCourse.setDescription(newCourse.getDescription());
            savedCourse.setCreatedBy(newCourse.getCreatedBy());
            courseRepository.save(savedCourse);
        }
    }

    @Transactional
    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }
}

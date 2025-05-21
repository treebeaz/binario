package com.binario.service;

import com.binario.entity.Course;
import com.binario.entity.User;
import com.binario.entity.UserCourse;
import com.binario.repository.CourseRepository;
import com.binario.repository.UserCourseRepository;
import com.binario.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserCourseService {
    private final UserCourseRepository userCourseRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public UserCourseService(UserCourseRepository userCourseRepository,
                             UserRepository userRepository,
                             CourseRepository courseRepository) {
        this.userCourseRepository = userCourseRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void enrollUserInCourse(Long userId, Long courseId) {
        if (userCourseRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new RuntimeException("Пользователь уже зачислен на этот курс");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Курс не найден"));

        UserCourse userCourse = new UserCourse(user, course);
        userCourseRepository.save(userCourse);
    }

    @Transactional(readOnly = true)
    public List<Course> getEnrolledCourses(Long userId) {
        return userCourseRepository.findEnrolledCoursesByUserId(userId)
                .stream()
                .map(UserCourse::getCourse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Course> getAvailableCourses(Long userId) {
        List<Course> enrolledCourses = getEnrolledCourses(userId);
        List<Course> allCourses = courseRepository.findAll();

        return allCourses.stream()
                .filter(course -> !enrolledCourses.contains(course))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isUserEnrolledInCourse(Long userId, Long courseId) {
        return userCourseRepository.isUserEnrolledInCourse(userId, courseId);
    }

    @Transactional(readOnly = true)
    public List<User> getStudentsByCourseId(Long courseId) {
        return userCourseRepository.findByCourseId(courseId)
                .stream()
                .map(UserCourse::getUser)
                .collect(Collectors.toList());
    }
}
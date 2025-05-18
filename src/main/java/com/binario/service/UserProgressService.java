package com.binario.service;

import com.binario.entity.CourseSection;
import com.binario.entity.UserProgress;
import com.binario.repository.CourseRepository;
import com.binario.repository.CourseSectionRepository;
import com.binario.repository.UserProgressRepository;
import com.binario.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserProgressService {
    private final UserProgressRepository userProgressRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseSectionRepository courseSectionRepository;

    public UserProgressService(UserProgressRepository userProgressRepository, UserRepository userRepository, CourseRepository courseRepository, CourseSectionRepository courseSectionRepository) {
        this.userProgressRepository = userProgressRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.courseSectionRepository = courseSectionRepository;
    }

    @Transactional
    public void startCourse(Long userId, Long courseId) {
        UserProgress progress = new UserProgress();
        progress.setUser(userRepository.findById(userId).orElseThrow());
        progress.setCourse(courseRepository.findById(courseId).orElseThrow());
        progress.setProgress(0.0);
        progress.setLastAccessed(LocalDateTime.now());

        userProgressRepository.save(progress);
    }


    @Transactional
    public UserProgress updateProgress(Long userId, Long courseId, double newProgress) {
        UserProgress progress = userProgressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new RuntimeException("Прогресс не найден"));

        progress.setProgress(newProgress);
        progress.setLastAccessed(LocalDateTime.now());

        return userProgressRepository.save(progress);
    }
}

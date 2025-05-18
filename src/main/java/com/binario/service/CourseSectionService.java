package com.binario.service;

import com.binario.entity.CourseSection;
import com.binario.entity.UserProgress;
import com.binario.repository.CourseSectionRepository;
import com.binario.repository.UserProgressRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseSectionService {
    private final CourseSectionRepository courseSectionRepository;
    private final UserProgressRepository userProgressRepository;

    public CourseSectionService(CourseSectionRepository courseSectionRepository, UserProgressRepository userProgressRepository) {
        this.courseSectionRepository = courseSectionRepository;
        this.userProgressRepository = userProgressRepository;
    }

    public List<CourseSection> getSectionsByCourseId(Long courseId) {
        return courseSectionRepository.findByCourseIdOrderByPositionAsc(courseId);
    }

    public boolean isLastSection(Long courseId, int position) {
        long totalSections = courseSectionRepository.countByCourseId(courseId);
        return position >= totalSections;
    }

    public CourseSection getSectionById(Long sectionId) {
        return courseSectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));
    }
}

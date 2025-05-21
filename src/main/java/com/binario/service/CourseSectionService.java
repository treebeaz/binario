package com.binario.service;

import com.binario.entity.Chapter;
import com.binario.entity.CourseSection;
import com.binario.repository.ChapterRepository;
import com.binario.repository.CourseSectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseSectionService {
    private final CourseSectionRepository courseSectionRepository;
    private final ChapterRepository chapterRepository;
    private final Logger logger = LoggerFactory.getLogger(CourseSectionService.class);


    public CourseSectionService(CourseSectionRepository courseSectionRepository, ChapterRepository chapterRepository) {
        this.courseSectionRepository = courseSectionRepository;
        this.chapterRepository = chapterRepository;
    }

    public List<CourseSection> getSectionsByCourseId(Long courseId) {
        return courseSectionRepository.findByCourseId(courseId);
    }

    public List<CourseSection> getAllSectionsByChapterId(Chapter chapter) {
        return courseSectionRepository.findByChapterIdOrderByOrderAsc(chapter.getId());
    }

    public List<Chapter> getChaptersWithSections(Long courseId) {
        return chapterRepository.findByCourseIdWithSections(courseId);
    }


    public CourseSection getSectionById(Long sectionId) {
        return courseSectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));
    }

    public int countByChapterId(Long chapterId) {
        return courseSectionRepository.countSectionByChapterId(chapterId);
    }

    @Transactional
    public void saveSection(CourseSection newSection) {
        if(newSection.getId() != null) {
            courseSectionRepository.save(newSection);
        }
        else {
            Integer nextOrder = getNextOrderNumber(newSection.getChapter().getId());

            CourseSection savedSection = new CourseSection();
            savedSection.setTitle(newSection.getTitle());
            savedSection.setDescription(newSection.getDescription());
            savedSection.setCourse(newSection.getCourse());
            savedSection.setContent(newSection.getContent());
            savedSection.setChapter(newSection.getChapter());
            savedSection.setOrder(nextOrder);
            courseSectionRepository.save(savedSection);
        }
    }

    private Integer getNextOrderNumber(Long chapterId) {
        Integer maxOrder = courseSectionRepository.findMaxOrderByChapterId(chapterId);
        return (maxOrder == null) ? 1 : maxOrder + 1;
    }
}

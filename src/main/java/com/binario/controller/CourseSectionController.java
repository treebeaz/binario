package com.binario.controller;

import com.binario.entity.Chapter;
import com.binario.entity.Course;
import com.binario.entity.CourseSection;
import com.binario.entity.User;
import com.binario.repository.ChapterRepository;
import com.binario.repository.CourseRepository;
import com.binario.service.CourseSectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/courses/{courseId}/sections")
public class CourseSectionController {

    private final CourseSectionService courseSectionService;
    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final Logger logger = LoggerFactory.getLogger(CourseSectionController.class);

    public CourseSectionController(CourseSectionService courseSectionService, 
                                 CourseRepository courseRepository, 
                                 ChapterRepository chapterRepository) {
        this.courseSectionService = courseSectionService;
        this.courseRepository = courseRepository;
        this.chapterRepository = chapterRepository;
    }



    // Показать список глав курса
    @GetMapping
    public String showChapters(@AuthenticationPrincipal User user,
                               @PathVariable Long courseId,
                               Model model) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<Chapter> chapters = courseSectionService.getChaptersWithSections(courseId);

        model.addAttribute("user", user);
        model.addAttribute("course", course);
        model.addAttribute("chapters", chapters);
        return "sections/chapters";
    }

    // Показать содержимое конкретной главы (все её разделы)
    @GetMapping("/chapter/{chapterId}")
    public String showChapterContent(@AuthenticationPrincipal User user,
                                     @PathVariable Long courseId,
                                     @PathVariable Long chapterId,
                                     Model model) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        List<CourseSection> sections = courseSectionService.getAllSectionsByChapterId(chapter);

        model.addAttribute("user", user);
        model.addAttribute("course", course);
        model.addAttribute("chapter", chapter);
        model.addAttribute("sections", sections);
        return "sections/chapter-content";
    }

    // Показать содержимое конкретного раздела
    @GetMapping("/{sectionId}")
    public String showSectionContent(@AuthenticationPrincipal User user,
                                     @PathVariable Long courseId,
                                     @PathVariable Long sectionId,
                                     Model model) {
        try {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            CourseSection section = courseSectionService.getSectionById(sectionId);

            model.addAttribute("user", user);
            model.addAttribute("course", course);
            model.addAttribute("section", section);
            return "sections/section-content";
        }
        catch (Exception e) {
            logger.error("Error loading section: {}", e.getMessage());
            throw e;
        }
    }
}

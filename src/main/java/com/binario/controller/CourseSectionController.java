package com.binario.controller;

import com.binario.entity.Course;
import com.binario.entity.CourseSection;
import com.binario.entity.User;
import com.binario.service.CourseSectionService;
import com.binario.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/courses/{courseId}/sections")
public class CourseSectionController {
    private final CourseSectionService courseSectionService;
    private final CourseService courseService;
    private static final Logger logger = LoggerFactory.getLogger(CourseSectionController.class);



    public CourseSectionController(CourseSectionService courseSectionService, CourseService courseService) {
        this.courseSectionService = courseSectionService;
        this.courseService = courseService;
    }

    @GetMapping
    public String showSections(@PathVariable Long courseId,
                               @AuthenticationPrincipal User user,
                               Model model, RedirectAttributes redirectAttributes) {

        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return "redirect:/courses?error=course_not_found";
        }

        List<CourseSection> sections = courseSectionService.getSectionsByCourseId(courseId);

        model.addAttribute("course", course);
        model.addAttribute("sections", sections);

        return "sections/list";
    }
}

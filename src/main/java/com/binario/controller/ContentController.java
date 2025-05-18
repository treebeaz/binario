package com.binario.controller;

import com.binario.entity.Course;
import com.binario.entity.CourseSection;
import com.binario.entity.User;
import com.binario.service.CourseSectionService;
import com.binario.service.CourseService;
import com.binario.service.UserProgressService;
import com.binario.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/courses/{courseId}/sections/{sectionId}")
public class ContentController {
    private final UserService userService;
    private CourseSectionService courseSectionService;
    private UserProgressService userProgressService;
    private CourseService courseService;

    public ContentController(CourseSectionService courseSectionService, UserProgressService userProgressService, CourseService courseService, UserService userService) {
        this.courseSectionService = courseSectionService;
        this.userProgressService = userProgressService;
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping
    public String showContent(@PathVariable Long courseId,
                              @PathVariable Long sectionId,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {

        User user = userService.findByUsername(userDetails.getUsername());

        Course course = courseService.getCourseById(courseId);
        CourseSection section = courseSectionService.getSectionById(sectionId);

        if(section == null) {
            return "redirect:/courses/" + courseId;
        }

        model.addAttribute("section", section);
        model.addAttribute("course", course);

//        boolean isLastSection = courseSectionService.isLastSection(courseId, section.getPosition());
//
//        if(isLastSection){
//            userProgressService.updateProgress(user.getId(), courseId, 100.00);
//        }

        return "sections/content";
    }
}

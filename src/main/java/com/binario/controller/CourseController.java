package com.binario.controller;

import com.binario.repository.CourseRepository;
import com.binario.service.CourseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public String showCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourse());
        return "courses/list";
    }
}

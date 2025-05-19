package com.binario.controller;

import com.binario.entity.User;
import com.binario.repository.CourseRepository;
import com.binario.service.CourseService;
import com.binario.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }
    // Список всех курсов
    @GetMapping
    public String showCourses(@AuthenticationPrincipal User user,
                              Model model) {
        model.addAttribute("courses", courseService.getAllCourse());
        model.addAttribute("user", user); // Добавляем пользователя в модель
        return "courses/list";
    }

//    // Детали курса с главами
//    @GetMapping("/{courseId}")
//    public String showCourseDetails(@PathVariable Long courseId,
//                                    @AuthenticationPrincipal User user,
//                                    Model model) {
//        model.addAttribute("course", courseService.getCourseById(courseId));
//        model.addAttribute("user", user);
//        return "courses/content";
//    }
}

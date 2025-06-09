package com.binario.controller;

import com.binario.entity.Course;
import com.binario.entity.User;
import com.binario.service.UserCourseService;
import com.binario.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {
    /**
     * Контроллер для обработки записи студента на курс
     */
    private final UserService userService;
    private final UserCourseService userCourseService;

    public StudentController(UserService userService, UserCourseService userCourseService) {
        this.userService = userService;
        this.userCourseService = userCourseService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<Course> availableCourse = userCourseService.getAvailableCourses(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("courses", availableCourse);

        return "student/dashboard";
    }

    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<Course> enrolledCourses = userCourseService.getEnrolledCourses(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("courses", enrolledCourses);
        return "student/profile";
    }

    @PostMapping("/courses/{courseId}/enroll")
    public String enrollInCourse(@PathVariable Long courseId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            userCourseService.enrollUserInCourse(user.getId(), courseId);
            redirectAttributes.addFlashAttribute("message", "Вы успешно записались на курс!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Не удалось записаться на курс: " + e.getMessage());
        }
        return "redirect:/student/dashboard";
    }

}

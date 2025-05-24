package com.binario.controller;

import com.binario.entity.Course;
import com.binario.entity.User;

import com.binario.service.CourseService;
import com.binario.service.UserCourseService;
import com.binario.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.binario.entity.UserTestAnswer;
import com.binario.service.UserTestAnswerService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    private final UserService userService;
    private final UserTestAnswerService userTestAnswerService;
    private final CourseService courseService;
    private final UserCourseService userCourseService;

    public TeacherController(UserService userService,
                             UserTestAnswerService userTestAnswerService,
                             CourseService courseService, UserCourseService userCourseService) {
        this.userService = userService;
        this.userTestAnswerService = userTestAnswerService;
        this.courseService = courseService;
        this.userCourseService = userCourseService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        return "teacher/dashboard";
    }

    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("courseTeacher", courseService.getCourseByTeacher(user));
        return "teacher/profile";
    }

    @GetMapping("/course/{courseId}/students")
    public String showCourseStudentsProgress(@PathVariable Long courseId,
                                             @AuthenticationPrincipal UserDetails userDetails,
                                             Model model) throws AccessDeniedException {
        User user = userService.findByUsername(userDetails.getUsername());
        Course course = courseService.getCourseById(courseId);

        if(!course.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("У вас нет доступа к этому курсу");
        }

        List<Object[]> progressList = userTestAnswerService.getStudentsProgressByCourse(courseId);

        model.addAttribute("course", course);
        model.addAttribute("progressList", progressList);

        return "teacher/course-students-progress";
    }

    @GetMapping("/code-reviews")
    public String getCodeReviews(Model model) {
        List<UserTestAnswer> unverifiedCodeAnswers = userTestAnswerService.findUnverifiedCodeAnswers();
        model.addAttribute("codeAnswers", unverifiedCodeAnswers);
        return "teacher/code-reviews";
    }
    
    @GetMapping("/code-review/{answerId}")
    public String getCodeReview(@PathVariable Long answerId, Model model) {
        UserTestAnswer answer = userTestAnswerService.findById(answerId);
        model.addAttribute("answer", answer);
        return "teacher/code-review";
    }
    
    @PostMapping("/code-review/{answerId}")
    public String submitCodeReview(
            @PathVariable Long answerId,
            @RequestParam Integer score,
            @RequestParam(required = false) String comment,
            RedirectAttributes redirectAttributes) {
        
        userTestAnswerService.reviewCodeAnswer(answerId, score, comment);
        redirectAttributes.addFlashAttribute("success", "Оценка успешно сохранена");
        return "redirect:/teacher/code-reviews";
    }
}
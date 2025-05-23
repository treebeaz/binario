package com.binario.controller;

import com.binario.entity.User;
import com.binario.service.UserService;
import org.hibernate.id.IncrementGenerator;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.binario.entity.UserTestAnswer;
import com.binario.service.UserTestAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    private final UserService userService;
    @Autowired
    private UserTestAnswerService userTestAnswerService;

    public TeacherController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        return "teacher/dashboard";
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
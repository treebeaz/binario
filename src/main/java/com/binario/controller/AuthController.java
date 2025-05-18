package com.binario.controller;

import com.binario.entity.User;
import com.binario.exception.UserAlreadyExistsException;
import com.binario.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showPageLogin() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showPageRegister(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, RedirectAttributes redirectAttributes) {

        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("message", "Регистрация прошла успешно");
            return "redirect:/auth/login";
        }
        catch (UserAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при регистрации");
            return "redirect:/auth/register";
        }

    }



}

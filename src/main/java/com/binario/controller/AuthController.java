package com.binario.controller;

import com.binario.entity.User;
import com.binario.exception.UserAlreadyExistsException;
import com.binario.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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

    @PostMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response,
                         RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            try {
                User user = userService.findByUsername(userDetails.getUsername());

                userService.deleteAccount(user);

                redirectAttributes.addFlashAttribute("success", "Аккаунт успешно удален");
            }
            catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
            }
        }

        new SecurityContextLogoutHandler().logout(request, response, authentication);
        return "redirect:/auth/login?logout";
    }
}

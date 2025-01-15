package com.project.library;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ✅ Afișare formular de înregistrare
    @GetMapping("/user/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "user_register";
    }

    // ✅ Procesare înregistrare cu fișier
    @PostMapping("/user/register")
    public String registerUser(@ModelAttribute("user") User user,
                               @RequestParam("idCardFile") MultipartFile idCardFile,
                               Model model) {
        try {
            String response = userService.registerUser(user, idCardFile);

            if (response.equals("Înregistrare realizată cu succes. Așteptați validarea contului!")) {
                model.addAttribute("successMessage", response);
                return "redirect:/user/login";
            } else {
                model.addAttribute("errorMessage", response);
                return "user_register";
            }
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Eroare la încărcarea cărții de identitate.");
            return "user_register";
        }
    }

    // ✅ Afișare formular de login
    @GetMapping("/user/login")
    public String showLoginForm() {
        return "user_login";
    }
}

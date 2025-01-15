package com.project.library;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    public AdminController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    // Afișează formularul de înregistrare
    @GetMapping("/admin/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("admin", new Admin());
        return "admin_register";
    }

    // Procesează datele completate în formular și redirecționează la login
    @PostMapping("/admin/register")
    public String registerAdmin(@ModelAttribute("admin") Admin admin, Model model) {
        String response = adminService.registerAdmin(admin);

        if (response.equals("Contul de administrator a fost creat cu succes!")) {
            return "redirect:/admin/login";  // Redirecționează corect către login
        } else {
            model.addAttribute("errorMessage", response);
            return "admin_register";
        }
    }

    // Afișează formularul de login
    @GetMapping("/admin/login")
    public String showLoginForm() {
        return "admin_login";
    }

    // Procesează datele de login
    @PostMapping("/admin/login")
    public String authenticateAdmin(@ModelAttribute("admin") Admin admin, Model model) {
        String response = adminService.authenticateAdmin(admin.getUsername(), admin.getPassword());

        if (response.equals("Autentificare reușită!")) {
            return "redirect:/admin/dashboard";  // Redirecționează la dashboard
        } else {
            model.addAttribute("errorMessage", response);
            return "admin_login";
        }
    }

    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();  // Obține username-ul utilizatorului logat

        model.addAttribute("username", username);

        return "admin_dashboard";  // Redirecționează către pagina de dashboard
    }

    @GetMapping("/admin/add-book")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "add_book";
    }

    @PostMapping("/admin/add-book")
    public String addBook(@ModelAttribute("book") Book book,
                          @RequestParam("pdfFile") MultipartFile pdfFile,
                          RedirectAttributes redirectAttributes) {
        try {
            String response = adminService.addBook(book, pdfFile);
            if (response.equals("Cartea a fost adăugată cu succes!")) {
                redirectAttributes.addFlashAttribute("successMessage", response);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", response);
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "A apărut o eroare la încărcarea fișierului PDF.");
        }
        return "redirect:/admin/add-book";  // Redirecționează pentru resetarea formularului
    }

    @GetMapping("/admin/validate-users")
    public String showInactiveUsers(Model model) {
        List<User> inactiveUsers = userService.getInactiveUsers();
        model.addAttribute("inactiveUsers", inactiveUsers);
        return "validate_users";  // Pagina de validare
    }

    @PostMapping("/admin/activate-user/{id}")
    public String activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return "redirect:/admin/validate-users?success=true";
    }



}
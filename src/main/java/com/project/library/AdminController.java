package com.project.library;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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


    @GetMapping("/id-card/{userId}")
    public ResponseEntity<Resource> downloadIdCard(@PathVariable Long userId) {
        User user = adminService.findUserById(userId);
        if (user.getIdCardUrl() == null) {
            throw new RuntimeException("Cartea de identitate nu a fost încărcată.");
        }

        try {
            Path filePath = Paths.get(user.getIdCardUrl());
            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Fișierul nu a putut fi descărcat.");
        }
    }

    // Afișează lista completă a utilizatorilor
    @GetMapping("/admin/manage-users")
    public String showAllUsers(Model model) {
        List<User> allUsers = adminService.getAllUsers();
        model.addAttribute("allUsers", allUsers);
        return "manage_users";  // Pagina pentru gestionarea utilizatorilor
    }

    // Șterge un utilizator după ID
    @PostMapping("/admin/delete-user/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String response = adminService.deleteUser(id);
        redirectAttributes.addFlashAttribute("message", response);
        return "redirect:/admin/manage-users";
    }

    // Afișează formularul de editare a utilizatorului
    @GetMapping("/admin/edit-user/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = adminService.findUserById(id);
        model.addAttribute("user", user);
        return "edit_user";
    }

    // Salvează modificările aduse utilizatorului
    @PostMapping("/admin/update-user/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") User updatedUser, RedirectAttributes redirectAttributes) {
        String response = adminService.updateUser(id, updatedUser);
        redirectAttributes.addFlashAttribute("message", response);
        return "redirect:/admin/manage-users";
    }

    // Afișează toate împrumuturile
    @GetMapping("/admin/view-loans")
    public String viewAllLoans(Model model) {
        List<Loan> allLoans = adminService.getAllLoans();
        model.addAttribute("allLoans", allLoans);
        return "view_loans";
    }

    // Confirmă returnarea unei cărți
    @PostMapping("/admin/confirm-return/{loanId}")
    public String confirmReturn(@PathVariable Long loanId, RedirectAttributes redirectAttributes) {
        String response = adminService.confirmReturn(loanId);
        redirectAttributes.addFlashAttribute("message", response);
        return "redirect:/admin/view-loans";
    }

    // Afișează toate cărțile
    @GetMapping("/admin/manage-books")
    public String showAllBooks(Model model) {
        List<Book> allBooks = adminService.getAllBooks();
        model.addAttribute("allBooks", allBooks);
        return "manage_books";
    }

    // Afișează formularul de editare a unei cărți
    @GetMapping("/admin/edit-book/{id}")
    public String showEditBookForm(@PathVariable Long id, Model model) {
        Book book = adminService.findBookById(id);
        model.addAttribute("book", book);
        return "edit_book";
    }

    // Procesează actualizarea unei cărți
    @PostMapping("/admin/edit-book/{id}")
    public String updateBook(@PathVariable Long id,
                             @ModelAttribute("book") Book updatedBook,
                             @RequestParam("pdfFile") MultipartFile pdfFile,
                             RedirectAttributes redirectAttributes) {
        try {
            String response = adminService.updateBook(id, updatedBook, pdfFile);
            redirectAttributes.addFlashAttribute("successMessage", response);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Eroare la încărcarea fișierului PDF.");
        }
        return "redirect:/admin/manage-books";
    }

    // Șterge o carte
    @PostMapping("/admin/delete-book/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String response = adminService.deleteBook(id);
        redirectAttributes.addFlashAttribute("message", response);
        return "redirect:/admin/manage-books";
    }


}
package com.project.library;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // ✅ Afișare împrumuturi active
    @GetMapping("/active-loans")
    public String viewActiveLoans(Model model) {
        loanService.updateActiveLoansPenalties();
        model.addAttribute("activeLoans", loanService.getActiveLoans());
        return "active_loans";
    }

    @PostMapping("/return-loan/{loanId}")
    public String confirmReturnBook(@PathVariable Long loanId) {
        loanService.returnBook(loanId);
        return "redirect:/admin/view-loans";
    }

    @Controller
    @RequestMapping("/user")
    public class UserLoanController {

        private final LoanService loanService;
        private final UserRepository userRepository;

        public UserLoanController(LoanService loanService, UserRepository userRepository) {
            this.loanService = loanService;
            this.userRepository = userRepository;
        }

        @GetMapping("/view-loans")
        public String viewUserLoans(Model model) {
            // Obține utilizatorul logat
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Obține utilizatorul fără Optional
            User user = userRepository.findByUsernameIgnoreCase(username);

            // Verifică dacă utilizatorul există
            if (user == null) {
                throw new RuntimeException("Utilizatorul nu a fost găsit.");
            }

            // Obține împrumuturile utilizatorului
            model.addAttribute("userLoans", loanService.getUserLoans(user));

            return "user_view_loans";
        }

    }

}

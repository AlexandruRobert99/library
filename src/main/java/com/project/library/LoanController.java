package com.project.library;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
        model.addAttribute("activeLoans", loanService.getActiveLoans());
        return "active_loans";
    }
}

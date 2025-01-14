package com.project.library;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }

    // Împrumută o carte
    @Transactional
    public String borrowBook(Long bookId, User user, LocalDate dueDate) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Cartea nu a fost găsită."));

        // Verifică dacă utilizatorul a împrumutat deja această carte și nu a returnat-o
        boolean alreadyBorrowed = loanRepository.existsByUserAndBookAndReturnDateIsNull(user, book);
        if (alreadyBorrowed) {
            return "Ai deja această carte împrumutată și nu a fost returnată.";
        }

        if (book.getPhysicalCopies() > 0) {
            // Scade numărul de exemplare disponibile
            book.setPhysicalCopies(book.getPhysicalCopies() - 1);
            bookRepository.save(book);

            // Creează împrumutul
            Loan loan = new Loan(book, user, LocalDate.now(), dueDate, null, 0);
            loanRepository.save(loan);

            return "Împrumutul a fost realizat cu succes!";
        } else {
            return "Nu există exemplare disponibile pentru această carte.";
        }
    }

    // Returnează o carte
    @Transactional
    public String returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Împrumutul nu a fost găsit."));

        if (loan.getReturnDate() != null) {
            return "Cartea a fost deja returnată.";
        }

        // Setează data de returnare
        loan.setReturnDate(LocalDate.now());

        // Crește numărul de exemplare disponibile
        Book book = loan.getBook();
        book.setPhysicalCopies(book.getPhysicalCopies() + 1);
        bookRepository.save(book);

        // Calculează penalizarea dacă este cazul
        if (loan.getDueDate().isBefore(LocalDate.now())) {
            long daysLate = LocalDate.now().toEpochDay() - loan.getDueDate().toEpochDay();
            int penalty = (int) (daysLate * 50);  // 50 lei/zi întârziere
            loan.setPenalty(penalty);
        }

        loanRepository.save(loan);
        return "Cartea a fost returnată cu succes!";
    }
}

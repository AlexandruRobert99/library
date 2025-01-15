package com.project.library;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public String borrowBook(Long bookId, User user, LocalDate loanDate, LocalDate dueDate) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Cartea nu a fost găsită."));

        // Verifică dacă utilizatorul a împrumutat deja această carte și nu a returnat-o
        boolean alreadyBorrowed = loanRepository.existsByUserAndBookAndReturnDateIsNull(user, book);
        if (alreadyBorrowed) {
            return "Acest utilizator are deja această carte împrumutată.";
        }

        if (book.getPhysicalCopies() > 0) {
            // Scade numărul de exemplare disponibile
            book.setPhysicalCopies(book.getPhysicalCopies() - 1);
            bookRepository.save(book);

            // Creează împrumutul cu data aleasă de admin
            Loan loan = new Loan(book, user, loanDate, dueDate, null, 0);
            loanRepository.save(loan);

            return "Împrumutul a fost realizat cu succes!";
        } else {
            return "Nu există exemplare disponibile pentru această carte.";
        }
    }




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
            long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
            int penalty = (int) (daysLate * 50);  // 50 lei/zi întârziere
            loan.setPenalty(penalty);
        }

        loanRepository.save(loan);
        return "Cartea a fost returnată cu succes!";
    }

    @Transactional
    public void updateActiveLoansPenalties() {
        List<Loan> activeLoans = loanRepository.findByReturnDateIsNull();
        LocalDate today = LocalDate.now();

        for (Loan loan : activeLoans) {
            if (loan.getDueDate().isBefore(today)) {
                long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), today);
                int penalty = (int) (daysLate * 50);  // 50 lei/zi întârziere
                loan.setPenalty(penalty);
                loanRepository.save(loan);
            } else {
                loan.setPenalty(0);  // Fără penalizare dacă nu a întârziat
                loanRepository.save(loan);
            }
        }
    }


    // Obține toate împrumuturile active (nereturnate)
    public List<Loan> getActiveLoans() {
        updateActiveLoansPenalties();
        return loanRepository.findByReturnDateIsNull();
    }


    // Obține toate împrumuturile returnate
    public List<Loan> getReturnedLoans() {
        return loanRepository.findByReturnDateIsNotNull();
    }

    public List<Loan> getUserLoans(User user) {
        return loanRepository.findByUser(user);
    }

    // Găsește împrumutul după ID
    public Loan findById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Împrumutul nu a fost găsit."));
    }

    // Actualizează împrumutul
    @Transactional
    public void updateLoan(Long id, Loan updatedLoan) {
        Loan loan = findById(id);
        loan.setLoanDate(updatedLoan.getLoanDate());
        loan.setDueDate(updatedLoan.getDueDate());
        loan.setReturnDate(updatedLoan.getReturnDate());
        loan.setPenalty(updatedLoan.getPenalty());
        loanRepository.save(loan);
    }

    // Șterge împrumutul
    @Transactional
    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }





}

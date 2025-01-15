package com.project.library;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // Împrumuturi active
    List<Loan> findByReturnDateIsNull();

    // Împrumuturi ale unui utilizator
    List<Loan> findByUser(User user);

    // Împrumuturi întârziate
    List<Loan> findByDueDateBeforeAndReturnDateIsNull(LocalDate currentDate);

    // Împrumuturi pentru o anumită carte
    List<Loan> findByBook(Book book);

    // Împrumuturi active pentru un utilizator
    List<Loan> findByUserAndReturnDateIsNull(User user);

    // Împrumuturi returnate de un utilizator
    List<Loan> findByUserAndReturnDateIsNotNull(User user);

    // Verifică dacă o carte este deja împrumutată de utilizator
    boolean existsByUserAndBookAndReturnDateIsNull(User user, Book book);

    // Împrumuturi cu penalizări active
    List<Loan> findByPenaltyGreaterThan(Integer penalty);

    // Împrumuturi returnate
    List<Loan> findByReturnDateIsNotNull();


}

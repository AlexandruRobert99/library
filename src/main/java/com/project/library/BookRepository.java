package com.project.library;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Căutare după titlu
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Căutare după autor
    List<Book> findByAuthorContainingIgnoreCase(String author);

    // Căutare după categorie
    List<Book> findByCategoryContainingIgnoreCase(String category);

    // Căutare după editură
    List<Book> findByPublisherContainingIgnoreCase(String publisher);

    // Căutare după colecție
    List<Book> findByCollectionContainingIgnoreCase(String collection);

    // Căutare după anul de apariție
    List<Book> findByReleaseDateBetween(LocalDate startDate, LocalDate endDate);

    // Căutare după ISBN
    Book findByIsbn(String isbn);

}

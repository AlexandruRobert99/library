package com.project.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class BookController {

    private final BookRepository bookRepository;

    @Autowired
    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Afișează toate cărțile
    @GetMapping("/books/all")
    public String viewAllBooks(Model model) {
        List<Book> books = bookRepository.findAll();
        model.addAttribute("books", books);
        return "view_all_books";
    }

    // Afișează pagina de căutare
    @GetMapping("/books/search")
    public String showSearchPage() {
        return "search_books";
    }

    // Căutare după titlu
    @GetMapping("/books/search/title")
    public String searchByTitle(@RequestParam("keyword") String title, Model model) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
        model.addAttribute("books", books);
        return "search_results";
    }

    // Căutare după autor
    @GetMapping("/books/search/author")
    public String searchByAuthor(@RequestParam("keyword") String author, Model model) {
        List<Book> books = bookRepository.findByAuthorContainingIgnoreCase(author);
        model.addAttribute("books", books);
        return "search_results";
    }

    // Căutare după categorie
    @GetMapping("/books/search/category")
    public String searchByCategory(@RequestParam("keyword") String category, Model model) {
        List<Book> books = bookRepository.findByCategoryContainingIgnoreCase(category);
        model.addAttribute("books", books);
        return "search_results";
    }

    // Căutare după editură
    @GetMapping("/books/search/publisher")
    public String searchByPublisher(@RequestParam("keyword") String publisher, Model model) {
        List<Book> books = bookRepository.findByPublisherContainingIgnoreCase(publisher);
        model.addAttribute("books", books);
        return "search_results";
    }

    // Căutare după colecție
    @GetMapping("/books/search/collection")
    public String searchByCollection(@RequestParam("keyword") String collection, Model model) {
        List<Book> books = bookRepository.findByCollectionContainingIgnoreCase(collection);
        model.addAttribute("books", books);
        return "search_results";
    }

    // Căutare după an de apariție
    @GetMapping("/books/search/year")
    public String searchByYear(@RequestParam("year") int year, Model model) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        List<Book> books = bookRepository.findByReleaseDateBetween(startDate, endDate);
        model.addAttribute("books", books);
        return "search_results";
    }
}

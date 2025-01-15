package com.project.library;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private static final String ID_CARD_STORAGE_PATH = "uploads/id_cards/";

    public UserService(UserRepository userRepository, BookRepository bookRepository,
                       LoanRepository loanRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    @Transactional
    public String registerUser(User user, MultipartFile idCardFile) throws IOException {
        if (userRepository.findByUsernameIgnoreCase(user.getUsername()) != null) {
            return "Username-ul este deja folosit!";
        }

        if (userRepository.findByEmailIgnoreCase(user.getEmail()) != null) {
            return "Email-ul este deja folosit!";
        }

        if (idCardFile == null || idCardFile.isEmpty()) {
            return "Cartea de identitate este necesară!";
        }

        String idCardPath = saveIdCard(idCardFile, user.getUsername());
        user.setIdCardUrl(idCardPath);
        user.setActive(false);
        userRepository.save(user);

        return "Înregistrare realizată cu succes. Așteptați validarea contului!";
    }

    @Transactional
    public String authenticateUser(String username, String password) {
        User user = userRepository.findByUsernameIgnoreCaseAndPassword(username, password);
        if (user == null) {
            return "Username sau parolă incorecte!";
        }
        if (!user.isActive()) {
            return "Contul nu a fost activat încă!";
        }
        return "Autentificare reușită!";
    }

    @Transactional
    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    @Transactional
    public List<Book> searchBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    @Transactional
    public List<Book> searchBooksByCategory(String category) {
        return bookRepository.findByCategoryContainingIgnoreCase(category);
    }

    @Transactional
    public String downloadPdf(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Cartea nu a fost găsită."));

        if (book.getPdfUrl() == null) {
            return "Această carte nu are un fișier PDF disponibil.";
        }

        return book.getPdfUrl();
    }

    private String saveIdCard(MultipartFile file, String username) throws IOException {
        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String uploadDir = ID_CARD_STORAGE_PATH + username + "/";
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath);
        return "/uploads/id_cards/" + username + "/" + uniqueFileName;
    }

    @Transactional
    public List<Loan> viewActiveLoans(User user) {
        return loanRepository.findByUserAndReturnDateIsNull(user);
    }

    @Transactional
    public List<Loan> viewReturnedLoans(User user) {
        return loanRepository.findByUserAndReturnDateIsNotNull(user);
    }

    // Găsește utilizatorii inactivi
    public List<User> getInactiveUsers() {
        return userRepository.findByActiveFalse();
    }

    // Activează un utilizator
    public void activateUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(true);
        userRepository.save(user);
    }
}

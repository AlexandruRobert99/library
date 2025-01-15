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
public class AdminService {

    private final AdminRepository adminRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    private static final String BOOK_PDF_STORAGE_PATH = "uploads/book_pdfs/";
    private static final String ID_CARD_STORAGE_PATH = "uploads/id_cards/";

    public AdminService(AdminRepository adminRepository, BookRepository bookRepository,
                        UserRepository userRepository, LoanRepository loanRepository) {
        this.adminRepository = adminRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
    }

    @Transactional
    public String authenticateAdmin(String username, String password) {
        Admin admin = adminRepository.findByUsernameIgnoreCaseAndPassword(username, password);
        if (admin == null) {
            return "Username sau parolă incorecte!";
        }
        return "Autentificare reușită!";
    }

    @Transactional
    public String registerAdmin(Admin admin) {
        if (adminRepository.findByUsernameIgnoreCase(admin.getUsername()) != null) {
            return "Username-ul de administrator este deja folosit!";
        }
        adminRepository.save(admin);
        return "Contul de administrator a fost creat cu succes!";
    }

    // Returnează toți utilizatorii
    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Actualizează datele unui utilizator
    @Transactional
    public String updateUser(Long userId, User updatedUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu a fost găsit."));

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setActive(updatedUser.isActive());

        userRepository.save(user);
        return "Utilizatorul a fost actualizat cu succes!";
    }


    @Transactional
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu a fost găsit."));
    }

    @Transactional
    public List<User> getInactiveUsers() {
        return userRepository.findByActiveFalse();
    }

    @Transactional
    public String addBook(Book book, MultipartFile pdfFile) throws IOException {
        if (bookRepository.findByIsbn(book.getIsbn()) != null) {
            return "ISBN-ul există deja!";
        }
        if (pdfFile != null && !pdfFile.isEmpty()) {
            String pdfPath = savePdfFile(pdfFile);
            book.setPdfUrl(pdfPath);
        }
        bookRepository.save(book);
        return "Cartea a fost adăugată cu succes!";
    }

    @Transactional
    public String updateBook(Long bookId, Book updatedBook, MultipartFile pdfFile) throws IOException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Cartea nu a fost găsită."));

        Book existingBook = bookRepository.findByIsbn(updatedBook.getIsbn());
        if (existingBook != null && !existingBook.getId().equals(bookId)) {
            return "Altă carte are deja acest ISBN!";
        }

        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setPublisher(updatedBook.getPublisher());
        book.setCollection(updatedBook.getCollection());
        book.setEdition(updatedBook.getEdition());
        book.setIsbn(updatedBook.getIsbn());
        book.setTranslator(updatedBook.getTranslator());
        book.setPages(updatedBook.getPages());
        book.setFormat(updatedBook.getFormat());
        book.setCoverType(updatedBook.getCoverType());
        book.setReleaseDate(updatedBook.getReleaseDate());
        book.setPhysicalCopies(updatedBook.getPhysicalCopies());
        book.setCategory(updatedBook.getCategory());

        if (pdfFile != null && !pdfFile.isEmpty()) {
            String pdfPath = savePdfFile(pdfFile);
            book.setPdfUrl(pdfPath);
        }

        bookRepository.save(book);
        return "Cartea a fost actualizată cu succes!";
    }

    @Transactional
    public String deleteBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            return "Cartea nu a fost găsită.";
        }
        bookRepository.deleteById(bookId);
        return "Cartea a fost ștearsă cu succes!";
    }

    @Transactional
    public String validateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu a fost găsit."));

        if (user.isActive()) {
            return "Contul este deja activat.";
        }

        user.setActive(true);
        userRepository.save(user);
        return "Contul a fost activat cu succes!";
    }

    @Transactional
    public String uploadIdCard(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu a fost găsit."));

        if (file.isEmpty()) {
            return "Fișierul este gol!";
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null ||
                !(originalFilename.endsWith(".pdf") || originalFilename.endsWith(".jpg") || originalFilename.endsWith(".png"))) {
            return "Format de fișier invalid! Acceptăm doar PDF, JPG sau PNG.";
        }

        String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;
        String uploadDir = ID_CARD_STORAGE_PATH + userId + "/";
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath);

        user.setIdCardUrl(filePath.toString());
        userRepository.save(user);

        return "Cartea de identitate a fost încărcată cu succes!";
    }

    private String savePdfFile(MultipartFile pdfFile) throws IOException {
        String originalFilename = pdfFile.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;
        Path uploadPath = Paths.get(BOOK_PDF_STORAGE_PATH);
        Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(pdfFile.getInputStream(), filePath);
        return filePath.toString();
    }

    @Transactional
    public String deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu a fost găsit."));

        if (!loanRepository.findByUserAndReturnDateIsNull(user).isEmpty()) {
            return "Utilizatorul are împrumuturi active și nu poate fi șters!";
        }

        userRepository.deleteById(userId);
        return "Utilizatorul a fost șters cu succes!";
    }

    // Returnează toate împrumuturile
    @Transactional
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    // Confirmă returnarea unei cărți
    @Transactional
    public String confirmReturn(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Împrumutul nu a fost găsit."));

        if (loan.getReturnDate() != null) {
            return "Cartea a fost deja returnată.";
        }

        loan.setReturnDate(java.time.LocalDate.now());
        loanRepository.save(loan);

        Book book = loan.getBook();
        book.setPhysicalCopies(book.getPhysicalCopies() + 1);
        bookRepository.save(book);

        return "Returnarea a fost confirmată cu succes!";
    }

    // Returnează toate cărțile
    @Transactional
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Găsește o carte după ID
    @Transactional
    public Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartea nu a fost găsită."));
    }

}

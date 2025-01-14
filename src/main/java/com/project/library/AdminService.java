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

    private static final String ID_CARD_STORAGE_PATH = "uploads/id_cards/";

    public AdminService(AdminRepository adminRepository, BookRepository bookRepository,
                        UserRepository userRepository, LoanRepository loanRepository) {
        this.adminRepository = adminRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
    }

    //  Autentificare Admin
    @Transactional
    public String authenticateAdmin(String username, String password) {
        Admin admin = adminRepository.findByUsernameIgnoreCaseAndPassword(username, password);
        if (admin == null) {
            return "Username sau parolă incorecte!";
        }
        return "Autentificare reușită!";
    }

    //  Adaugă o carte nouă cu verificare ISBN
    @Transactional
    public String addBook(Book book) {
        if (bookRepository.findByIsbn(book.getIsbn()) != null) {
            return "ISBN-ul există deja!";
        }
        bookRepository.save(book);
        return "Cartea a fost adăugată cu succes!";
    }

    //  Editează o carte existentă cu verificare ISBN duplicat
    @Transactional
    public String updateBook(Long bookId, Book updatedBook) {
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
        book.setPdfUrl(updatedBook.getPdfUrl());
        book.setPhysicalCopies(updatedBook.getPhysicalCopies());
        book.setCategory(updatedBook.getCategory());

        bookRepository.save(book);
        return "Cartea a fost actualizată cu succes!";
    }

    //  Șterge o carte
    @Transactional
    public String deleteBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            return "Cartea nu a fost găsită.";
        }
        bookRepository.deleteById(bookId);
        return "Cartea a fost ștearsă cu succes!";
    }

    //  Validează un utilizator (activează contul)
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

    //  Încarcă și validează cartea de identitate
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

    //  Șterge un utilizator și fișierul ID dacă nu are împrumuturi active
    @Transactional
    public String deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu a fost găsit."));

        if (!loanRepository.findByUserAndReturnDateIsNull(user).isEmpty()) {
            return "Utilizatorul are împrumuturi active și nu poate fi șters!";
        }

        try {
            Path idCardPath = Paths.get(user.getIdCardUrl());
            Files.deleteIfExists(idCardPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        userRepository.deleteById(userId);
        return "Utilizatorul a fost șters cu succes!";
    }
}

package com.project.library;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String author;
    private String publisher;
    private String collection;
    private String edition;

    @Column(unique = true)
    private String isbn;

    private String translator;
    private Integer pages;
    private String format;
    private String coverType;
    private LocalDate releaseDate;
    private String pdfUrl;
    private Integer physicalCopies;
    private String category;

    public Book() {
    }

    public Book(String title, String author, String publisher, String collection, String edition, String isbn,
                String translator, Integer pages, String format, String coverType, LocalDate releaseDate,
                String pdfUrl, Integer physicalCopies, String category) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.collection = collection;
        this.edition = edition;
        this.isbn = isbn;
        this.translator = translator;
        this.pages = pages;
        this.format = format;
        this.coverType = coverType;
        this.releaseDate = releaseDate;
        this.pdfUrl = pdfUrl;
        this.physicalCopies = physicalCopies;
        this.category = category;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getCoverType() {
        return coverType;
    }

    public void setCoverType(String coverType) {
        this.coverType = coverType;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public Integer getPhysicalCopies() {
        return physicalCopies;
    }

    public void setPhysicalCopies(Integer physicalCopies) {
        this.physicalCopies = physicalCopies;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

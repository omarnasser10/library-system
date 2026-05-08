package com.library.dto;

public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private Integer totalCopies;
    private Integer availableCopies;
    private String category;
    private String coverImageUrl;
    private String pdfUrl;

    public BookResponse() {}

    public BookResponse(Long id, String title, String author, Integer totalCopies, Integer availableCopies, String category, String coverImageUrl, String pdfUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.category = category;
        this.coverImageUrl = coverImageUrl;
        this.pdfUrl = pdfUrl;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public Integer getTotalCopies() { return totalCopies; }
    public Integer getAvailableCopies() { return availableCopies; }
    public String getCategory() { return category; }
    public String getCoverImageUrl() { return coverImageUrl; }
    
    public String getPdfUrl() { 
        if (this.pdfUrl != null && !this.pdfUrl.isEmpty()) {
            return "/books/" + this.id + "/read";
        }
        return null;
    }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setTotalCopies(Integer totalCopies) { this.totalCopies = totalCopies; }
    public void setAvailableCopies(Integer availableCopies) { this.availableCopies = availableCopies; }
    public void setCategory(String category) { this.category = category; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }
}
package com.library.dto;

import jakarta.validation.constraints.Min;

public class UpdateBookRequest {

    private String title;

    private String author;

    @Min(value = 1, message = "Total copies must be at least 1")
    private Integer totalCopies;

    private String category;
    private String coverImageUrl;

    public UpdateBookRequest() {}

    public UpdateBookRequest(String title, String author, Integer totalCopies, String category, String coverImageUrl) {
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.category = category;
        this.coverImageUrl = coverImageUrl;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Integer getTotalCopies() { return totalCopies; }
    public void setTotalCopies(Integer totalCopies) { this.totalCopies = totalCopies; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
}

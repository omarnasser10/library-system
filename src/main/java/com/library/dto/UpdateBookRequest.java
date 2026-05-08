package com.library.dto;

import jakarta.validation.constraints.Min;

public class UpdateBookRequest {

    private String title;

    private String author;

    @Min(value = 1, message = "Total copies must be at least 1")
    private Integer totalCopies;

    public UpdateBookRequest() {}

    public UpdateBookRequest(String title, String author, Integer totalCopies) {
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Integer getTotalCopies() { return totalCopies; }
    public void setTotalCopies(Integer totalCopies) { this.totalCopies = totalCopies; }
}

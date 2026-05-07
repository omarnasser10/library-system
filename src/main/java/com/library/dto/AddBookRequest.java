package com.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class AddBookRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @Min(value = 1, message = "Total copies must be at least 1")
    private Integer totalCopies;

    public AddBookRequest() {}

    public AddBookRequest(String title, String author, Integer totalCopies) {
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
package com.library.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private Integer totalCopies;
    private Integer availableCopies;
    private String category;
    private String coverImageUrl;
    private String pdfUrl;

    private Boolean active;
}
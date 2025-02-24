package com.example.libraryManagement.LibraryManagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "madhav_borrowed_book")

public class BorrowedBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Books book;

    @Column(name = "borrowed_date")
    private Date borrowedDate;

    @Column(name = "return_date")
    private Date returnDate;


    @Column(name = "is_returned", columnDefinition = "BIT DEFAULT 0")
    private Boolean isReturned;
}

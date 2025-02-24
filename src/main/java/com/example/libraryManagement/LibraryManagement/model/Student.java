package com.example.libraryManagement.LibraryManagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "madhav_Student") // Ensure this matches your table name
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id") // Map to the correct column name
    private Long studentId;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "otp") // Optional: specify if you have OTP
    private String otp;

    @Column(name = "verified")
    private Boolean verified;

    @Column(name = "borrowed_books_count") // Ensure this matches the column name
    private Integer borrowedBooksCount;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<BorrowedBook> borrowedBooks; // List of borrowed books


}

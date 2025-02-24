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
@Table(name = "madhav_Reservation") // Matches the database table name
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; // References the Student entity who made the reservation

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Books book; // References the Books entity for the reserved book

    @Column(name = "reservation_date", nullable = false)
    private Date reservationDate; // The date when the reservation was made
}

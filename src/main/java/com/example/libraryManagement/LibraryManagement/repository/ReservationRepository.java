package com.example.libraryManagement.LibraryManagement.repository;

import com.example.libraryManagement.LibraryManagement.model.Books;
import com.example.libraryManagement.LibraryManagement.model.Reservation;
import com.example.libraryManagement.LibraryManagement.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByBookIdOrderByReservationDateAsc(Long id);
    List<Reservation> findByStudentAndBook(Student student, Books book);

    //boolean existsByStudentIdAndBookId(Long studentId, Long bookId);
}


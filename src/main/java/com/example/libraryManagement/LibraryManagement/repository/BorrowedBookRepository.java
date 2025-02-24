package com.example.libraryManagement.LibraryManagement.repository;

import com.example.libraryManagement.LibraryManagement.model.BorrowedBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface BorrowedBookRepository extends JpaRepository<BorrowedBook, Long> {
}

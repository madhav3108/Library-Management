package com.example.libraryManagement.LibraryManagement.repository;

import com.example.libraryManagement.LibraryManagement.model.Books;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository  extends JpaRepository<Books, Long> {
    boolean existsByTitle(String title);
    List<Books> findByAuthorId(Long authorId);
    List<Books> findByTitleContainingIgnoreCase(String title);
}

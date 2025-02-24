package com.example.libraryManagement.LibraryManagement.service;

import com.example.libraryManagement.LibraryManagement.dto.BookDto;
import com.example.libraryManagement.LibraryManagement.model.Books;
import com.example.libraryManagement.LibraryManagement.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    @Autowired
    private BookRepository bookRepository;

    // All books in Library and search book by Title
    public Page<BookDto> getAllBooks(int page, int size) {
        return bookRepository.findAll(PageRequest.of(page, size))
                .map(this::mapToBookDto);  // Map the result to BookDto
    }

    public List<BookDto> searchBooksByTitle(String title) {
        List<Books> books = bookRepository.findByTitleContainingIgnoreCase(title);
        return books.stream().map(this::mapToBookDto).collect(Collectors.toList());
    }

    // Helper method to map Books to BookDto
    private BookDto mapToBookDto(Books book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getDescription(),
                book.getAuthorId()
        );
    }
}

package com.example.libraryManagement.LibraryManagement.controller;

import com.example.libraryManagement.LibraryManagement.dto.BookDto;
import com.example.libraryManagement.LibraryManagement.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/library")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/books")
    public ResponseEntity<?> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title) {

        if (title != null && !title.isEmpty()) {
            List<BookDto> books = libraryService.searchBooksByTitle(title);
            return ResponseEntity.ok(books);
        }

        Page<BookDto> books = libraryService.getAllBooks(page, size);
        return ResponseEntity.ok(books);
    }
}

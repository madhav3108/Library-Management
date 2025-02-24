package com.example.libraryManagement.LibraryManagement.controller;

import com.example.libraryManagement.LibraryManagement.dto.BookDto;
import com.example.libraryManagement.LibraryManagement.dto.LoginResponseDto;
import com.example.libraryManagement.LibraryManagement.dto.RegisterDto;
import com.example.libraryManagement.LibraryManagement.service.AuthorRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AuthorController {

    @Autowired
    private AuthorRegistration authorRegistration;

    // Register a new author
    @PostMapping("/authors/register")
    public ResponseEntity<String> register(@Validated @RequestBody RegisterDto registerDto) {
        try {
            authorRegistration.initiateRegistration(registerDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Registration successful. An OTP has been sent to your email.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Verify the OTP sent to the author's email
    @PostMapping("/authors/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        try {
            boolean isVerified = authorRegistration.completeRegistration(email, otp);
            return isVerified ? ResponseEntity.ok("OTP verification successful")
                    : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP or user not verified");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    // Login method that returns a JWT token
    @PostMapping("/authors/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody RegisterDto loginRequest) {
        try {
            String token = authorRegistration.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(new LoginResponseDto("Login successful", token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponseDto(e.getMessage(), null));
        }
    }

    // Add a book for the logged-in author
    @PostMapping("/authors/books")
    public ResponseEntity<BookDto> addBookForAuthor(@RequestHeader("Authorization") String authorizationHeader, @RequestBody BookDto bookDTO) {
        String token = extractToken(authorizationHeader);
        try {
            BookDto savedBook = authorRegistration.addBookForAuthor(token, bookDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Get all books for the logged-in author
    @GetMapping("/authors/books")
    public ResponseEntity<List<BookDto>> getBooksByAuthor(@RequestHeader("Authorization") String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        try {
            List<BookDto> books = authorRegistration.getBooksForAuthor(token);
            return ResponseEntity.ok(books);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Admin approval for author registration
    @PostMapping("/admin/approve-author/{authorId}")
    public ResponseEntity<String> approveAuthor(@PathVariable Long authorId, @RequestParam Long adminId, @RequestParam boolean approve) {
        try {
            authorRegistration.approveAuthor(authorId, adminId, approve);
            return ResponseEntity.ok("Author approval status updated");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private String extractToken(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ")
                ? authorizationHeader.substring(7)
                : null;
    }
}

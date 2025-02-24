package com.example.libraryManagement.LibraryManagement.controller;

import com.example.libraryManagement.LibraryManagement.dto.LoginDto;
import com.example.libraryManagement.LibraryManagement.dto.RegisterDto;
import com.example.libraryManagement.LibraryManagement.dto.StudentDto;
import com.example.libraryManagement.LibraryManagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Validated @RequestBody RegisterDto registerDto) {
        try {
            studentService.register(registerDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful. An OTP has been sent to your email.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        try {
            boolean isVerified = studentService.completeVerification(email, otp);
            return ResponseEntity.ok("OTP verification successful");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Validated @RequestBody LoginDto loginRequest) {
        try {
            String token = studentService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/borrow/{bookId}")
    public ResponseEntity<String> borrowBook(@RequestHeader("Authorization") String token, @PathVariable Long bookId) {
        token=token.substring(7);
        try {
            studentService.borrowBook(token, bookId);
            return ResponseEntity.ok("Book borrowed successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/reserve/{bookId}")
    public ResponseEntity<String> reserveBook(@RequestHeader("Authorization") String token, @PathVariable Long bookId) {
        token=token.substring(7);
        try {
            studentService.reserveBook(token, bookId);
            return ResponseEntity.ok("Book reserved successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/return/{borrowedBookId}/{studentId}")
    public ResponseEntity<String> returnBook(@PathVariable Long borrowedBookId,
                                             @PathVariable Long studentId) {
        System.out.println("Received request to return book with ID: " + borrowedBookId);

        try {
            // Pass the borrowedBookId and studentId to the service method
            studentService.returnBook(borrowedBookId, studentId);
            return ResponseEntity.ok("Book returned successfully.");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}

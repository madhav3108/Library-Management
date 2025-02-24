package com.example.libraryManagement.LibraryManagement.service;

import com.example.libraryManagement.LibraryManagement.dto.RegisterDto;
import com.example.libraryManagement.LibraryManagement.dto.StudentDto;
import com.example.libraryManagement.LibraryManagement.email.EmailService;
import com.example.libraryManagement.LibraryManagement.model.Books;
import com.example.libraryManagement.LibraryManagement.model.BorrowedBook;
import com.example.libraryManagement.LibraryManagement.model.Reservation;
import com.example.libraryManagement.LibraryManagement.model.Student;
import com.example.libraryManagement.LibraryManagement.repository.BookRepository;
import com.example.libraryManagement.LibraryManagement.repository.BorrowedBookRepository;
import com.example.libraryManagement.LibraryManagement.repository.ReservationRepository;
import com.example.libraryManagement.LibraryManagement.repository.StudentRepository;
import com.example.libraryManagement.LibraryManagement.security.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentService {

    private static final int MAX_BORROW_LIMIT = 10;
    private static final Long AUTHOR_STUDENT_ID = 1L;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowedBookRepository borrowedBookRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtHelper jwtHelper;


    // Map between DTO and Model
    private Student mapToStudentModel(RegisterDto registerDto) {
        Student student = new Student();
        student.setName(registerDto.getName());
        student.setEmail(registerDto.getEmail());
        student.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        student.setVerified(false);
        student.setBorrowedBooksCount(0);
        return student;
    }

    private StudentDto mapToStudentDto(Student student) {
        StudentDto studentDto = new StudentDto();
        studentDto.setName(student.getName());
        studentDto.setEmail(student.getEmail());
        studentDto.setBorrowedBooksCount(student.getBorrowedBooksCount());
        studentDto.setVerified(student.getVerified());
        return studentDto;
    }

    // Register Student
    public void register(RegisterDto registerDto) {
        if (studentRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new RuntimeException("Student already exists with this email: " + registerDto.getEmail());
        }

        Student student = mapToStudentModel(registerDto);
        String otp = generateOtp();
        student.setOtp(otp);
        emailService.sendEmail(student.getEmail(), "Verify your email", "Your OTP is: " + otp);
        studentRepository.save(student);
    }

    // Generate the OTP
    private String generateOtp() {
        Random rand = new Random();
        return String.format("%06d", rand.nextInt(1000000));
    }

    // After verify OTP , registration completed
    public boolean completeVerification(String email, String otp) {
        Optional<Student> studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            if (student.getOtp().equals(otp) && !student.getVerified()) {
                student.setVerified(true);
                studentRepository.save(student);
                return true;
            } else if (student.getVerified()) {
                throw new RuntimeException("User is already verified.");
            } else {
                throw new RuntimeException("Invalid OTP.");
            }
        } else {
            throw new RuntimeException("Student not found.");
        }
    }
    // Login
    public String login(String email, String password) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!student.getVerified()) {
            throw new RuntimeException("Student is not verified. Please check your email for the OTP.");
        }

        if (!passwordEncoder.matches(password, student.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Generate he token for particular email id
        return jwtHelper.generateToken(student.getEmail());
    }

    // Borrow a Book
    public void borrowBook(String token, Long bookId) {
        String email = extractEmailFromToken(token);
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Books book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getAuthorId().equals(student.getStudentId())) {
            throw new RuntimeException("You cannot borrow your own book.");
        }

        if (book.getIsBorrowed() != null && book.getIsBorrowed() && !student.getStudentId().equals(AUTHOR_STUDENT_ID)) {
            throw new RuntimeException("Book is already borrowed by the author.");
        }

        if (student.getBorrowedBooksCount() >= MAX_BORROW_LIMIT) {
            throw new RuntimeException("You cannot borrow more than " + MAX_BORROW_LIMIT + " books");
        }

        BorrowedBook borrowedBook = new BorrowedBook();
        borrowedBook.setStudent(student);
        borrowedBook.setBook(book);
        borrowedBook.setBorrowedDate(new Date());
        borrowedBook.setIsReturned(false);
        borrowedBookRepository.save(borrowedBook);

        book.setIsBorrowed(true);
        bookRepository.save(book);

        student.setBorrowedBooksCount(student.getBorrowedBooksCount() + 1);
        studentRepository.save(student);
    }

    // reserve a book
    public void reserveBook(String token, Long bookId) {
        String email = extractEmailFromToken(token);
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Books book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getAuthorId().equals(student.getStudentId())) {
            throw new RuntimeException("You cannot reserve your own book.");
        }

        List<Reservation> existingReservations = reservationRepository.findByStudentAndBook(student, book);
        if (!existingReservations.isEmpty()) {
            throw new RuntimeException("You have already reserved this book.");
        }

        if (book.getIsBorrowed() != null && book.getIsBorrowed()) {
            Reservation reservation = new Reservation();
            reservation.setStudent(student);
            reservation.setBook(book);
            reservation.setReservationDate(new Date());
            reservationRepository.save(reservation);
        } else {
            throw new RuntimeException("Book is available, no need to reserve it.");
        }
    }



    // return a book
    public void returnBook(Long borrowedBookId, Long studentId) {
        System.out.println("Attempting to return book with ID: " + borrowedBookId);

        // Retrieve the borrowed book entry
        BorrowedBook borrowedBook = borrowedBookRepository.findById(borrowedBookId)
                .orElseThrow(() -> new RuntimeException("Borrowed book not found for ID: " + borrowedBookId));

        // Check if the student ID matches the one who borrowed the book
        if (!borrowedBook.getStudent().getStudentId().equals(studentId)) {
            throw new RuntimeException("You are not authorized to return this book.");
        }

        // Check if the book has already been returned
        if (borrowedBook.getIsReturned()) {
            throw new RuntimeException("This book has already been returned.");
        }

        // Check if the book has been borrowed for more than 15 days
        long borrowedTime = new Date().getTime() - borrowedBook.getBorrowedDate().getTime();
        long borrowedDays = borrowedTime / (1000 * 60 * 60 * 24); // Convert milliseconds to days

        if (borrowedDays > 15) {
            System.out.println("Book is overdue by " + (borrowedDays - 15) + " days.");
            applyFine(studentId, borrowedDays - 15); // Example method for applying fines
        }

        // Mark the book as returned
        borrowedBook.setIsReturned(true);
        borrowedBook.setReturnDate(new Date());

        // Update the student’s borrowed book count
        Student student = borrowedBook.getStudent();
        student.setBorrowedBooksCount(student.getBorrowedBooksCount() - 1);
        studentRepository.save(student);

        // Mark the book as available
        Books book = borrowedBook.getBook();
        book.setIsBorrowed(false);
        bookRepository.save(book);

        // Notify reservations
        List<Reservation> reservations = reservationRepository.findByBookIdOrderByReservationDateAsc(book.getId());
        for (Reservation reservation : reservations) {
            sendNotificationEmail(reservation.getStudent().getStudentId(), book.getTitle());
            reservationRepository.delete(reservation);
        }

        borrowedBookRepository.save(borrowedBook);
        System.out.println("Book returned successfully.");
    }


    private void applyFine(Long studentId, long overdueDays) {
        System.out.println("Applying fine for student " + studentId + " for " + overdueDays + " overdue days.");
    }



    // Email notification
    private void sendNotificationEmail(Long studentId, String bookTitle) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String emailContent = "Dear " + student.getName() + ", the book titled '" + bookTitle + "' is now available for you.";
        emailService.sendEmail(student.getEmail(), "Book Available for Borrow", emailContent);
    }

    public String extractEmailFromToken(String token) {
        return jwtHelper.getUsernameFromToken(token);  // Assuming your JwtHelper has this method to get the email/username
    }
}


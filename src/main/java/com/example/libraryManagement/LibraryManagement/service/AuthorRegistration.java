package com.example.libraryManagement.LibraryManagement.service;

import com.example.libraryManagement.LibraryManagement.dto.BookDto;
import com.example.libraryManagement.LibraryManagement.dto.RegisterDto;
import com.example.libraryManagement.LibraryManagement.email.EmailService;
import com.example.libraryManagement.LibraryManagement.model.AdminModel;
import com.example.libraryManagement.LibraryManagement.model.AuthorModel;
import com.example.libraryManagement.LibraryManagement.model.Books;
import com.example.libraryManagement.LibraryManagement.repository.AuthorRepository;
import com.example.libraryManagement.LibraryManagement.repository.BookRepository;
import com.example.libraryManagement.LibraryManagement.repository.AdminRepository;
import com.example.libraryManagement.LibraryManagement.security.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthorRegistration {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtHelper jwtHelper;

    public void initiateRegistration(RegisterDto registerDto) {
        if (authorRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new RuntimeException("Author already exists with this email: " + registerDto.getEmail());
        }

        AuthorModel author = new AuthorModel();
        author.setName(registerDto.getName());
        author.setEmail(registerDto.getEmail());
        author.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        author.setVerified(false); // Default to false until OTP is verified

        String otp = generateOtp();
        author.setOtp(otp);
        emailService.sendEmail(author.getEmail(), "Verify your email", "Your OTP is: " + otp);
        authorRepository.save(author);

        notifyAdminsForApproval();
    }

    public boolean completeRegistration(String email, String otp) {
        Optional<AuthorModel> authorOpt = authorRepository.findByEmail(email);
        if (authorOpt.isPresent()) {
            AuthorModel author = authorOpt.get();
            if (author.getOtp().equals(otp) && !author.getVerified()) {
                author.setVerified(true);
                authorRepository.save(author);
                return true;
            } else if (author.getVerified()) {
                throw new RuntimeException("User is already verified.");
            } else {
                throw new RuntimeException("Invalid OTP.");
            }
        } else {
            throw new RuntimeException("Author not found.");
        }
    }

    public String login(String email, String password) {
        AuthorModel author = authorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!author.getVerified()) {
            throw new RuntimeException("Author is not verified. Please check your email for the OTP.");
        }

        if (!passwordEncoder.matches(password, author.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (!author.getApproved()) {
            throw new RuntimeException("Author is not approved by an admin.");
        }

        return jwtHelper.generateToken(author.getEmail());
    }

    public BookDto addBookForAuthor(String token, BookDto bookDTO) {
        String email = jwtHelper.getUsernameFromToken(token);

        AuthorModel author = authorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        if (!author.getVerified()) {
            throw new RuntimeException("Only verified authors can add books.");
        }

        if (!author.getApproved()) {
            throw new RuntimeException("Only approved authors can add books.");
        }

        if (bookRepository.existsByTitle(bookDTO.getTitle())) {
            throw new RuntimeException("A book with the title '" + bookDTO.getTitle() + "' already exists.");
        }

        Books book = new Books();
        book.setTitle(bookDTO.getTitle());
        book.setDescription(bookDTO.getDescription());
        book.setAuthorId(author.getId());

        Books savedBook = bookRepository.save(book);

        return new BookDto(savedBook.getId(), savedBook.getTitle(), savedBook.getDescription(), savedBook.getAuthorId());
    }

    public List<BookDto> getBooksForAuthor(String token) {
        String email = jwtHelper.getUsernameFromToken(token);

        AuthorModel author = authorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        if (!author.getApproved()) {
            throw new RuntimeException("Only approved authors can view their books.");
        }

        List<Books> books = bookRepository.findByAuthorId(author.getId());

        return books.stream().map(book -> new BookDto(
                book.getId(),
                book.getTitle(),
                book.getDescription(),
                book.getAuthorId()
        )).toList();
    }

    private void notifyAdminsForApproval() {
        List<AdminModel> admins = adminRepository.findAll();

        if (admins.isEmpty()) {
            throw new RuntimeException("No admins found in the system to notify.");
        }

        Random random = new Random();
        AdminModel selectedAdmin = admins.get(random.nextInt(admins.size()));

        String message = "New author registration request pending approval for " + selectedAdmin.getName();
        emailService.sendEmail(selectedAdmin.getEmail(), "New Author Registration Request", message);
    }


    public void approveAuthor(Long authorId, Long adminId, boolean approve) {
        AuthorModel author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        AdminModel admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (author.getApproved()) {
            throw new RuntimeException("Author is already approved.");
        }

        if (approve) {
            // Set the author to approved status
            author.setApproved(true);
            author.setVerified(true);
            author.setRole(AuthorModel.Role.AUTHOR);
            author.setApprovedBy(admin);

            authorRepository.save(author);

            emailService.sendEmail(author.getEmail(), "Author Account Approved",
                    "Your account has been approved by " + admin.getName());
        } else {
            // If not approved, delete the author
            authorRepository.delete(author);
            emailService.sendEmail(author.getEmail(), "Author Account Rejected",
                    "Your account registration has been rejected by " + admin.getName());
        }
    }


    private String generateOtp() {
        Random rand = new Random();
        int otp = rand.nextInt(999999);
        return String.format("%06d", otp);
    }
}


package com.example.libraryManagement.LibraryManagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "madhav_Author")
public class AuthorModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Auto-generated ID

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "verified")
    private Boolean verified = false;  // For OTP verification, default to false

    @Column(name = "otp")
    private String otp; // For OTP verification

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.PENDING; // Default role is PENDING until approved by admin

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private AdminModel approvedBy;  // Reference to the AdminModel who approved the registration

    @Column(name = "author_id", nullable = true)  // Nullable, as author ID might not be set initially
    private Integer authorId;

    @Column(name = "approved")
    private Boolean approved = false;  // Default to false until approval

    // Enum for role management
    public enum Role {
        ADMIN, AUTHOR, PENDING
    }
}

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
@Table(name = "madhav_Book", uniqueConstraints = @UniqueConstraint(columnNames = "title"))
public class Books {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "is_borrowed")
    private Boolean isBorrowed = false;

    public Boolean isBorrowed() {
        return isBorrowed;
    }

    public void setBorrowed(Boolean isBorrowed) {
        this.isBorrowed = isBorrowed;
    }
}

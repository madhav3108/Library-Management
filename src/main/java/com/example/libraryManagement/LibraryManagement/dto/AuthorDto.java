package com.example.libraryManagement.LibraryManagement.dto;

public class AuthorDto {
    private String name;
    private String email;
    private long author_Id;
    private boolean verified;

    // Getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public long getAuthor_Id() {
        return author_Id;
    }

    public boolean isVerified() {
        return verified;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAuthor_Id(long author_Id) {
        this.author_Id = author_Id;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}

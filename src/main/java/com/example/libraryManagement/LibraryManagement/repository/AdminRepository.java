package com.example.libraryManagement.LibraryManagement.repository;

import com.example.libraryManagement.LibraryManagement.model.AdminModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminModel, Long> {

    // Custom query to find admin by email (if required)
    Optional<AdminModel> findByEmail(String email);
}

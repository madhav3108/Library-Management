package com.example.libraryManagement.LibraryManagement;

import org.springframework.boot.SpringApplication;

public class TestLibraryManagementApplication {

	public static void main(String[] args) {
		SpringApplication.from(LibraryManagementApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

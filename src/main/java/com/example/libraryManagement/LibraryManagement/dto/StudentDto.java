//package com.example.libraryManagement.LibraryManagement.dto;
//
//import lombok.*;
//
//@Getter
//@Setter
//@ToString
//@NoArgsConstructor
//@AllArgsConstructor
//public class StudentDto {
//
//    private Long studentId;
//    private String name;
//    private String email;
//    private String password;
//    private Boolean verified;
//
//    public void setBorrowedBooksCount(Integer borrowedBooksCount) {
//    }
//}

package com.example.libraryManagement.LibraryManagement.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {
    private String name;
    private String email;
    private Integer borrowedBooksCount;
    private Boolean verified;
}


package com.secchamp.chal.entity;

import jakarta.persistence.*;  
import lombok.Data;

@Entity
@Data  // Lombok will generate the required getters and setters
public class User {

    @Id  // Primary key annotation
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private Integer role; // 0: admin, 1: user

    @Column(nullable = false)
    private String createDate;

    private String lastUpdateDate;
    private String lastLogin;
}

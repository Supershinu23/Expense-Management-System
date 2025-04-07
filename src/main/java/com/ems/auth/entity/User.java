package com.ems.auth.entity;

import com.ems.auth.model.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String username;
    private String email;
    private String mobile;

    private String password;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    private boolean verified = false; // Updated on OTP success
}

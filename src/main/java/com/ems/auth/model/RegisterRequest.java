package com.ems.auth.model;

import lombok.Data;

@Data
public class RegisterRequest {
    private String fullName;
    private String username;
    private String email;
    private String mobile;
    private String password;
}

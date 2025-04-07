package com.ems.auth.service;

import com.ems.auth.model.LoginRequest;
import com.ems.auth.model.RegisterRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface AuthService {

    void registerUser(RegisterRequest request);

    boolean verifyOtp(String mobile, String otp);

    String login(LoginRequest request) throws JsonProcessingException;
}

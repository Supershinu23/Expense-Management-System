package com.ems.auth.model;

import lombok.Data;

@Data
public class OtpVerificationRequest {
    private String mobile;
    private String otp;
}

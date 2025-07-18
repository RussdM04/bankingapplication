package com.example.banking.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OtpStorageService {

    // Stores OTP against username (temporary, in-memory)
    private final Map<String, String> otpStore = new HashMap<>();

    public void saveOtp(String username, String otp) {
        otpStore.put(username, otp);
    }

    public String getOtp(String username) {
        return otpStore.get(username);
    }

    public void clearOtp(String username) {
        otpStore.remove(username);
    }
}
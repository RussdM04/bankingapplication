package com.example.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP for Transaction");
        message.setText("Your OTP is: " + otp + "\n\nIt is valid for 5 minutes.");
        mailSender.send(message);
    }
    public void sendFraudAlert(String toEmail, double amount, String accountType, String accountNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("⚠️ Suspicious Withdrawal Alert");
        message.setText("Dear user,\n\n"
                + "We detected a high-value withdrawal of ₹" + amount
                + " from your " + accountType + " account (" + accountNumber + ").\n"
                + "If this wasn't you, please contact support immediately.\n\n"
                + "Stay safe,\nYour Bank Security Team");
        mailSender.send(message);
    }
}

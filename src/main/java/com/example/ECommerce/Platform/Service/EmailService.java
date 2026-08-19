package com.example.ECommerce.Platform.Service;

import com.example.ECommerce.Platform.Exception.EmailSendException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String toEmail, String otp) {


        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            // message.setFrom("your_email@gmail.com"); it automatically taken byspringboot from application properties
            message.setSubject("Password Reset OTP");

            message.setText(
                    "Hello,\n\n" +
                            "Your OTP for password reset is: " + otp + "\n" +
                            "Valid for 2 minutes.\n\n" +
                            "If you did not request this, ignore it.\n\n" +
                            "Thanks."
            );
            mailSender.send(message);
        } catch (MailException e) {
            throw new EmailSendException("Failed to send OTP email");
        }
    }
}

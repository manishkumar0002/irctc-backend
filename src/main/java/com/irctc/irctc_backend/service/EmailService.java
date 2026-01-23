package com.irctc.irctc_backend.service;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendBookingConfirmation(String toEmail, String pnr) {

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(toEmail);
        mail.setSubject("IRCTC Ticket Confirmed");
        mail.setText(
                "Your ticket has been CONFIRMED.\n\n" +
                        "PNR: " + pnr + "\n\n" +
                        "Happy Journey 🚆"
        );

        mailSender.send(mail);
    }
}

package com.project.notes_v2.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    private static final String CREATE_ACCOUNT_SUBJECT = "Confirmation account creation on notes-write.ovh";
    private static final String UPDATE_ACCOUNT_SUBJECT = "Confirmation account update on notes-write.ovh";

    public void sendConfirmationEmail(String destinationEmail, String username, boolean isCreation) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setFrom("notes.write.app@gmail.com");
        helper.setTo(destinationEmail);
        String subject;
        String htmlMsg;
        if(isCreation) {
            subject = CREATE_ACCOUNT_SUBJECT;
            htmlMsg = this.setTextAccountCreation(destinationEmail, username);
        } else {
            // Update case
            subject = UPDATE_ACCOUNT_SUBJECT;
            htmlMsg = this.setTextAccountUpdate(destinationEmail, username);
        }
        helper.setText(htmlMsg, true);
        helper.setSubject(subject);
        mailSender.send(mimeMessage);
    }

    public void sendLostPasswordEmail(String destinationEmail, String password) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setFrom("notes.write.app@gmail.com");
        helper.setTo(destinationEmail);
        String subject = "Lost password";
        String htmlMsg = setTextLostPassword(password);
        helper.setText(htmlMsg, true);
        helper.setSubject(subject);
        mailSender.send(mimeMessage);
    }

    private String setTextAccountCreation(String destinationEmail, String username) {
        return "<h3>Account creation notes-write.ovh</h3>" +
                "<p>This is a confirmation email.</p>" +
                "<p>An account on the application notes-write.ovh has been created</p>" +
                "<p>Email    : " + destinationEmail + "</p> " +
                "<p>Username : " + username + "</p> " +
                "<p>Thank you.</p>" +
                "<p>Romain Cotoni</p>" +
                "<p>Linkedin : <a href='https://www.linkedin.com/in/romain-cotoni/'>romain-cotoni</a></p>" +
                "<p>Github : <a href='https://github.com/romain-cotoni'>romain-cotoni</a></p>";
    }

    private String setTextAccountUpdate(String destinationEmail, String username) {
        return "<h3>Account update notes-write.ovh</h3>" +
                "<p>This is a confirmation email.</p>" +
                "<p>An account on the application notes-write.ovh has been updated</p>" +
                "<p>Email    : " + destinationEmail + "</p> " +
                "<p>Username : " + username + "</p> " +
                "<p>Thank you.</p>" +
                "<p>Romain Cotoni</p>" +
                "<p>Linkedin : <a href='https://www.linkedin.com/in/romain-cotoni/'>romain-cotoni</a></p>" +
                "<p>Github : <a href='https://github.com/romain-cotoni'>romain-cotoni</a></p>";
    }

    private String setTextLostPassword(String password) {
        return "<h3>Account update notes-write.ovh</h3>" +
                "<p>This is a requested email.</p>" +
                "<p>Please find as requested a reminder of your password</p>" +
                "<p>Password    : " + password + "</p> " +
                "<p>Thank you.</p>" +
                "<p>Romain Cotoni</p>" +
                "<p>Linkedin : <a href='https://www.linkedin.com/in/romain-cotoni/'>romain-cotoni</a></p>" +
                "<p>Github : <a href='https://github.com/romain-cotoni'>romain-cotoni</a></p>";
    }



}

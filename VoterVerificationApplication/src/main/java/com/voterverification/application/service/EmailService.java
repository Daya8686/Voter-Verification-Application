package com.voterverification.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordEmail(String recipientEmail, String resetLink, String firstName) throws MessagingException {
    	MimeMessage message = mailSender.createMimeMessage();

        // Use MimeMessageHelper to set the email properties
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Set the recipient email, subject, and sender
        helper.setTo(recipientEmail);
        helper.setSubject("Password Reset Request");

        // HTML content of the email
        String htmlContent = "<!DOCTYPE html><html lang=\"en\">"
                + "<head><meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>Password Reset Request</title>"
                + "<style>body {font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;}"
                + " .email-container {background-color: #ffffff; max-width: 600px; margin: 0 auto; padding: 20px;"
                + " border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);} .header {text-align: center; "
                + "font-size: 24px; font-weight: bold; color: #333; margin-bottom: 20px;} "
                + ".content {font-size: 16px; color: #000000; line-height: 1.6;} "
                + ".reset-link {display: inline-block; padding: 10px 20px; margin-top: 20px;"
                + " font-size: 16px; background-color: #66ff99; color: #000000;"
                + " text-decoration: none; border-radius: 5px;} "
                + ".reset-link:hover { background-color: #ff0066; color: #000000} "
                + ".footer {margin-top: 30px; text-align: center; font-size: 14px; color: #888;}</style></head>"
                + "<body><div class=\"email-container\"><div class=\"header\">Password Reset Request</div>"
                + "<div class=\"content\"><p>Hello,"+firstName+"</p><p>We received a request to reset your password. "
                + "If you did not request this change, please ignore this email.</p>"
                + "<p>Click the following link to reset your password:</p>"
                + "<p><a href=\"" + resetLink + "\" class=\"reset-link\">Reset Password</a></p>"
                + "<p>This link will expire at 4 AM. After that, you'll need to request a new password reset.</p></div>"
                + "<div class=\"footer\"><p>&copy; 2025 Voter Verification Application. All rights reserved.</p></div></div></body></html>";

        
        helper.setText(htmlContent, true); 

        mailSender.send(message);
    }
    
    
    public void sendUserVerificationEmail(String recipientEmail, String verificationLink, String firstName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        // Use MimeMessageHelper to set the email properties
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Set the recipient email, subject, and sender
        helper.setTo(recipientEmail);
        helper.setSubject("User Verification Request");

        // HTML content of the email
        String htmlContent = "<!DOCTYPE html><html lang=\"en\">"
                + "<head><meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>User Verification Request</title>"
                + "<style>body {font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;} "
                + ".email-container {background-color: #ffffff; max-width: 600px; margin: 0 auto; padding: 20px; "
                + "border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);} .header {text-align: center; "
                + "font-size: 24px; font-weight: bold; color: #333; margin-bottom: 20px;} "
                + ".content {font-size: 16px; color: #000000; line-height: 1.6;} "
                + ".verification-link {display: inline-block; padding: 10px 20px; margin-top: 20px; "
                + "font-size: 16px; background-color: #66ff99; color: #ffffff; "
                + "text-decoration: none; border-radius: 5px;} "
                + ".verification-link:hover { background-color: #ff0066; color: #ffffff} "
                + ".footer {margin-top: 30px; text-align: center; font-size: 14px; color: #888;}</style></head>"
                + "<body><div class=\"email-container\"><div class=\"header\">User Verification Request</div>"
                + "<div class=\"content\"><p>Hello, " + firstName + "</p><p>Thank you for registering with us. "
                + "To complete your registration, please verify your email address by clicking the link below.</p>"
                + "<p>Click the following link to verify your email address:</p>"
                + "<p><a href=\"" + verificationLink + "\" class=\"verification-link\">Verify Email</a></p>"
                + "<div style=\"background-color: yellow; font-weight: bold; color: Black; padding: 10px;\">"
                + "<p>This is Email verification link! Also ask Admin of the application to provide access to use the application.</p> </div>"
                + "<p>This link will expire at 4 AM. After that, you'll need to request a new verification link.</p></div>"
                + "<div class=\"footer\"><p>&copy; 2025 Voter Verification Application. All rights reserved.</p></div></div></body></html>";

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }


    public void sendUserVerificationSuccessEmail(String recipientEmail, String firstName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        // Use MimeMessageHelper to set the email properties
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Set the recipient email, subject, and sender
        helper.setTo(recipientEmail);
        helper.setSubject("User Verified Successfully");

        // HTML content of the email
        String htmlContent = "<!DOCTYPE html><html lang=\"en\">"
                + "<head><meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>User Verified Successfully</title>"
                + "<style>body {font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;} "
                + ".email-container {background-color: #ffffff; max-width: 600px; margin: 0 auto; padding: 20px; "
                + "border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);} .header {text-align: center; "
                + "font-size: 24px; font-weight: bold; color: #333; margin-bottom: 20px;} "
                + ".content {font-size: 16px; color: #000000; line-height: 1.6;} "
                + ".footer {margin-top: 30px; text-align: center; font-size: 14px; color: #888;}</style></head>"
                + "<body><div class=\"email-container\"><div class=\"header\">User Verified Successfully</div>"
                + "<div class=\"content\"><p>Hello, " + firstName + "</p><p>Congratulations! Your email address has been successfully verified.</p>"
                + "<div style=\"background-color: yellow; font-weight: bold; color: Black; padding: 10px;\">"
                + "<p>Email verification done! Also ask Admin of the application to provide access to use the application</p> </div>"
                + "<p>You can access all the features of your account after your account is unlocked by admin! . If you did not perform this action, please contact us immediately.</p></div>"
                + "<div class=\"footer\"><p>&copy; 2025 Voter Verification Application. All rights reserved.</p></div></div></body></html>";

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
    public void sendUpdateEmailVerificationSuccessEmail(String recipientEmail, String firstName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        // Use MimeMessageHelper to set the email properties
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Set the recipient email, subject, and sender
        helper.setTo(recipientEmail);
        helper.setSubject("Email Updated Successfully");

        // HTML content of the email
        String htmlContent = "<!DOCTYPE html><html lang=\"en\">"
                + "<head><meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>User Verified Successfully</title>"
                + "<style>body {font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;} "
                + ".email-container {background-color: #ffffff; max-width: 600px; margin: 0 auto; padding: 20px; "
                + "border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);} .header {text-align: center; "
                + "font-size: 24px; font-weight: bold; color: #333; margin-bottom: 20px;} "
                + ".content {font-size: 16px; color: #000000; line-height: 1.6;} "
                + ".footer {margin-top: 30px; text-align: center; font-size: 14px; color: #888;}</style></head>"
                + "<body><div class=\"email-container\"><div class=\"header\">User Verified Successfully</div>"
                + "<div class=\"content\"><p>Hello, " + firstName + "</p><p>Congratulations! Your email address has been successfully verified.</p>"
                + "<div style=\"background-color: yellow; font-weight: bold; color: Black; padding: 10px;\">"
                + "<p>Email verification done!</p> </div>"
                + "<p>You can access all the features. If you did not perform this action, please contact us immediately.</p></div>"
                + "<div class=\"footer\"><p>&copy; 2025 Voter Verification Application. All rights reserved.</p></div></div></body></html>";

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
    
    public void sendLoginSuccessEmail(String recipientEmail, String firstName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        // Use MimeMessageHelper to set the email properties
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Set the recipient email, subject, and sender
        helper.setTo(recipientEmail);
        helper.setSubject("Login Successful");

        // HTML content of the email
        String htmlContent = "<!DOCTYPE html><html lang=\"en\">"
                + "<head><meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>Login Successful</title>"
                + "<style>body {font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;} "
                + ".email-container {background-color: #ffffff; max-width: 600px; margin: 0 auto; padding: 20px; "
                + "border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);} .header {text-align: center; "
                + "font-size: 24px; font-weight: bold; color: #333; margin-bottom: 20px;} "
                + ".content {font-size: 16px; color: #000000; line-height: 1.6;} "
                + ".footer {margin-top: 30px; text-align: center; font-size: 14px; color: #888;}</style></head>"
                + "<body><div class=\"email-container\"><div class=\"header\">Login Successful</div>"
                + "<div class=\"content\"><p>Hello, " + firstName + "</p><p>Congratulations! You have successfully logged into your account.</p>"
                + "<div style=\"background-color: green; font-weight: bold; color: white; padding: 10px;\">"
                + "<p>Your login was successful, and you can now access all your features.</p> </div>"
                + "<p>If you did not perform this action, please contact us immediately.</p></div>"
                + "<div class=\"footer\"><p>&copy; 2025 Voter Verification Application. All rights reserved.</p></div></div></body></html>";

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    public void sendLogoutSuccessEmail(String recipientEmail, String firstName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        // Use MimeMessageHelper to set the email properties
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Set the recipient email, subject, and sender
        helper.setTo(recipientEmail);
        helper.setSubject("Logout Successful");

        // HTML content of the email
        String htmlContent = "<!DOCTYPE html><html lang=\"en\">"
                + "<head><meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>Logout Successful</title>"
                + "<style>body {font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;} "
                + ".email-container {background-color: #ffffff; max-width: 600px; margin: 0 auto; padding: 20px; "
                + "border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);} .header {text-align: center; "
                + "font-size: 24px; font-weight: bold; color: #333; margin-bottom: 20px;} "
                + ".content {font-size: 16px; color: #000000; line-height: 1.6;} "
                + ".footer {margin-top: 30px; text-align: center; font-size: 14px; color: #888;}</style></head>"
                + "<body><div class=\"email-container\"><div class=\"header\">Logout Successful</div>"
                + "<div class=\"content\"><p>Hello, " + firstName + "</p><p>You have successfully logged out from your account.</p>"
                + "<div style=\"background-color: red; font-weight: bold; color: white; padding: 10px;\">"
                + "<p>Your logout was successful, and you are no longer logged into the application.</p> </div>"
                + "<p>If you did not perform this action, please contact us immediately.</p></div>"
                + "<div class=\"footer\"><p>&copy; 2025 Voter Verification Application. All rights reserved.</p></div></div></body></html>";

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }



}

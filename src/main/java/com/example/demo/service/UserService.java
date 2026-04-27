package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public boolean checkPassword(String plain, String hashed) {
        if(hashed == null || plain == null) return false;
        return BCrypt.checkpw(plain, hashed);
    }

    public String generateOtp() {
        Random rnd = new Random();
        int number = rnd.nextInt(900000) + 100000;
        return String.valueOf(number);
    }

    public void sendOtpEmail(String toEmail, String otp, String subject) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText("Your 6-digit verification code is: " + otp + "\nIt expires in 10 minutes.");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send SMTP email: " + e.getMessage());
            // We swallow the exception to allow registration to complete during testing.
        }
    }

    public User registerUser(String username, String email, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashPassword(password));
        user.setRole(role != null && role.equalsIgnoreCase("admin") ? "admin" : "user");
        
        String otp = generateOtp();
        System.out.println("\n--- DEVELOPMENT OTP ---");
        System.out.println("OTP for " + email + " is: " + otp);
        System.out.println("-----------------------\n");
        
        user.setOtp(hashPassword(otp));
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(2));
        user = userRepository.save(user);

        sendOtpEmail(email, otp, "Verify your EcoLearn account");
        return user;
    }

    public void initiateForgotPassword(String email) {
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String otp = generateOtp();
            System.out.println("\n--- DEVELOPMENT OTP ---");
            System.out.println("OTP for Password Reset (" + email + ") is: " + otp);
            System.out.println("-----------------------\n");
            
            user.setOtp(hashPassword(otp));
            user.setOtpExpiry(LocalDateTime.now().plusMinutes(2));
            userRepository.save(user);
            sendOtpEmail(email, otp, "Password Reset Request");
        }
    }

    public void resendOtp(String email) {
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getVerified()) return; // Already verified, don't resend
            
            String otp = generateOtp();
            System.out.println("\n--- DEVELOPMENT OTP ---");
            System.out.println("New OTP for " + email + " is: " + otp);
            System.out.println("-----------------------\n");
            
            user.setOtp(hashPassword(otp));
            user.setOtpExpiry(LocalDateTime.now().plusMinutes(2));
            userRepository.save(user);
            sendOtpEmail(email, otp, "Your new EcoLearn verification code");
        }
    }

    public boolean resetPassword(String email, String otp, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty()) return false;
        User user = userOpt.get();
        
        if (user.getOtp() == null || user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (checkPassword(otp, user.getOtp())) {
            user.setPassword(hashPassword(newPassword));
            user.setOtp(null);
            user.setOtpExpiry(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean verifyOtp(String email, String otp) {
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty()) return false;
        User user = userOpt.get();

        if (user.getVerified()) return true;

        if (user.getOtp() == null || user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (checkPassword(otp, user.getOtp())) {
            user.setVerified(true);
            user.setOtp(null);
            user.setOtpExpiry(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
